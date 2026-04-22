package com.easypark.app.core.data.repository

import com.easypark.app.core.data.dao.AppEventDao
import com.easypark.app.core.data.entity.AppEventEntity
import com.easypark.app.core.data.remote.FirebaseManager
import kotlinx.datetime.Clock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AppEventRepository(
    private val appEventDao: AppEventDao,
    private val firebaseManager: FirebaseManager
) {
    suspend fun logEvent(type: String) {
        val event = AppEventEntity(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            type = type
        )
        appEventDao.insertEvent(event)
        syncEvents()
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
                // Si falla la sincronización (ej. sin internet), se queda como unsynced
                continue
            }
        }
        
        if (successfullySynced.isNotEmpty()) {
            appEventDao.updateEvents(successfullySynced)
        }
    }
}
