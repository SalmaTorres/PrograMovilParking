package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.notifications.NotificationHelper
import kotlinx.coroutines.delay

class TestReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // Enviamos la notificación
            NotificationHelper.showNotification(
                context = applicationContext,
                title = "EasyPark",
                message = "¡Tu reserva de parqueo terminará en 15 minutos!"
            )
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}
