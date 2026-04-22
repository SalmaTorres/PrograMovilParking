package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.repository.AppEventRepository
import com.easypark.app.core.notifications.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: AppEventRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            val deletedCount = repository.checkAndCleanupEvents()
            
            if (deletedCount > 0) {
                NotificationHelper.showNotification(
                    applicationContext,
                    "Limpieza de Base de Datos",
                    "Se han eliminado $deletedCount registros antiguos para ahorrar espacio."
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
