package com.easypark.app.core.work

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.easypark.app.core.data.remote.RemoteConfigManager
import java.util.concurrent.TimeUnit
import kotlin.math.max

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
            resolveAbTestingIntervalMinutes(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DataSyncWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )
    }

    actual fun scheduleDailyCleanup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val cleanupRequest = PeriodicWorkRequest.Builder(
            DailyCleanupWorker::class.java,
            1,
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

    private fun resolveAbTestingIntervalMinutes(): Long {
        val remoteConfig = RemoteConfigManager()
        if (!remoteConfig.getBoolean(RemoteAbTestKeys.ENABLED)) {
            return RemoteAbTestKeys.DEFAULT_GROUP_A_FREQUENCY_MINUTES
        }

        val group = remoteConfig.getString(RemoteAbTestKeys.GROUP).trim().uppercase()
        val frequencyKey = if (group == RemoteAbTestKeys.GROUP_B) {
            RemoteAbTestKeys.GROUP_B_FREQUENCY_MINUTES
        } else {
            RemoteAbTestKeys.GROUP_A_FREQUENCY_MINUTES
        }
        val configuredMinutes = remoteConfig.getString(frequencyKey).toLongOrNull()
            ?: RemoteAbTestKeys.DEFAULT_GROUP_A_FREQUENCY_MINUTES

        return max(15L, configuredMinutes)
    }
}

@Composable
actual fun rememberBackgroundTaskManager(): BackgroundTaskManager {
    val context = LocalContext.current
    return remember { BackgroundTaskManager(context) }
}
