package com.easypark.app.core.work

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

import androidx.work.PeriodicWorkRequest
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.Constraints
import androidx.work.NetworkType

actual class BackgroundTaskManager(private val context: Context) {
    
    actual fun scheduleTestReminder() {
        val workRequest = OneTimeWorkRequestBuilder<TestReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    actual fun schedulePeriodicDataSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequest.Builder(
            DataSyncWorker::class.java,
            15, // Intervalo mínimo es de 15 minutos en Android
            TimeUnit.MINUTES
        )
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DataSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    actual fun scheduleDailyCleanup() {
        // Restricción: Solo con WiFi
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val cleanupRequest = PeriodicWorkRequest.Builder(
            DailyCleanupWorker::class.java,
            1, // 1 vez al día
            TimeUnit.DAYS
        )
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyCleanupWork",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}

@Composable
actual fun rememberBackgroundTaskManager(): BackgroundTaskManager {
    val context = LocalContext.current
    return remember { BackgroundTaskManager(context) }
}
