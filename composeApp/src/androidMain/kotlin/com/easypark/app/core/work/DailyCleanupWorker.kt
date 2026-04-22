package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.core.notifications.NotificationHelper
import kotlinx.coroutines.delay

class DailyCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            println("DailyCleanupWorker: 🧹 Iniciando tarea de limpieza automática...")
            
            // 1. Inicializar Remote Config para obtener el límite actual
            val remoteConfigManager = RemoteConfigManager()
            remoteConfigManager.initialize()
            val maxRecordsStr = remoteConfigManager.getString("max_local_records")
            val maxRecords = maxRecordsStr.toIntOrNull() ?: 50
            
            // 2. Conectar a la base de datos
            val database = createDatabase(getDatabaseBuilder(applicationContext))
            val appEventDao = database.appEventDao()
            
            // 3. Verificar y Limpiar
            val currentCount = appEventDao.getEventCount()
            if (currentCount > maxRecords) {
                val toDelete = currentCount - maxRecords
                appEventDao.deleteOldestEvents(toDelete)
                
                println("DailyCleanupWorker: Se eliminaron $toDelete registros antiguos.")
                
                // 4. Notificar al usuario
                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = "Limpieza de Caché",
                    message = "Se ha liberado espacio eliminando $toDelete registros de eventos antiguos."
                )
            } else {
                println("DailyCleanupWorker: No es necesario limpiar. Registros actuales: $currentCount, Límite: $maxRecords")
            }
            
            Result.success()
        } catch (e: Exception) {
            println("DailyCleanupWorker: ❌ Error durante la limpieza - ${e.message}")
            Result.failure()
        }
    }
}
