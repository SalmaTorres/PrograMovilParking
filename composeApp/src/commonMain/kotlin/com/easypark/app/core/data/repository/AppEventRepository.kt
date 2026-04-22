package com.easypark.app.core.data.repository

import com.easypark.app.core.data.dao.AppEventDao
import com.easypark.app.core.data.entity.AppEventEntity
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.data.remote.RemoteConfigManager
import kotlinx.datetime.Clock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AppEventRepository(
    private val appEventDao: AppEventDao,
    private val firebaseManager: FirebaseManager,
    private val remoteConfigManager: RemoteConfigManager
) {
    suspend fun logEvent(type: String) {
        val event = AppEventEntity(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            type = type
        )
        appEventDao.insertEvent(event)
        
        // Sincronizar con la nube
        syncEvents()
        
        // Forzar la limpieza local INMEDIATAMENTE después de cada evento
        val deletedCount = checkAndCleanupEvents()
        if (deletedCount > 0) {
            println("AppEventRepository: 🧹 Se eliminaron $deletedCount registros (Limpieza por evento).")
        }
    }

    suspend fun syncEvents() {
        val unsyncedEvents = appEventDao.getUnsyncedEvents()
        if (unsyncedEvents.isEmpty()) return

        val successfullySynced = mutableListOf<AppEventEntity>()

        for (event in unsyncedEvents) {
            try {
                val eventJson = buildJsonObject {
                    put("timestamp", event.timestamp)
                    put("type", event.type)
                }
                firebaseManager.saveData("app_events/${event.timestamp}", eventJson.toString())
                successfullySynced.add(event.copy(synced = true))
            } catch (e: Exception) {
                continue
            }
        }
        
        if (successfullySynced.isNotEmpty()) {
            appEventDao.updateEvents(successfullySynced)
        }
    }

    suspend fun checkAndCleanupEvents(): Int {
        return try {
            // Asegurarse de inicializar para obtener el valor más reciente
            remoteConfigManager.initialize() 
            
            val maxRecordsStr = remoteConfigManager.getString("max_local_records")
            // Si no lee nada o hay error, bajamos el default a 5 para la prueba
            val maxRecords = maxRecordsStr.toIntOrNull() ?: 5 
            
            val currentCount = appEventDao.getEventCount()
            println("AppEventRepository: Registros actuales: $currentCount, Límite: $maxRecords")
            
            if (currentCount > maxRecords) {
                val toDelete = currentCount - maxRecords
                appEventDao.deleteOldestEvents(toDelete)
                return toDelete
            }
            0
        } catch (e: Exception) {
            println("AppEventRepository: Error en limpieza - ${e.message}")
            0
        }
    }
}
