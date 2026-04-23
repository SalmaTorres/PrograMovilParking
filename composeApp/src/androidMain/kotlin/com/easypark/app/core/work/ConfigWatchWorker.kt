package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import com.easypark.app.core.data.entity.RemoteConfigEntity
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.core.notifications.NotificationHelper

class ConfigWatchWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            println("ConfigWatchWorker: Auditando Firebase Remote Config...")

            val configKey = "mensaje_alerta"

            // Get Remote Config directly
            val remoteConfigManager = RemoteConfigManager()
            remoteConfigManager.initialize() // Esperará el fetch
            
            val newValue = remoteConfigManager.getString(configKey)

            val database = createDatabase(getDatabaseBuilder(applicationContext))
            val dao = database.remoteConfigDao()
            val oldValueEntity = dao.getConfig(configKey)

            if (oldValueEntity == null || oldValueEntity.configValue != newValue) {
                if (newValue.isNotBlank()) {
                    // Solo notificar si hay un mensaje real y distinto al de antes
                    NotificationHelper.showNotification(
                        applicationContext,
                        "¡Actualización de EasyPark!",
                        newValue
                    )
                }
                
                // Save new value
                dao.insertConfig(
                    RemoteConfigEntity(configKey, newValue)
                )
            }

            println("ConfigWatchWorker: Auditoría de Config finalizada.")
            Result.success()
        } catch (e: Exception) {
            println("ConfigWatchWorker: Error obteniendo Remote Config: ${e.message}")
            Result.retry()
        }
    }
}
