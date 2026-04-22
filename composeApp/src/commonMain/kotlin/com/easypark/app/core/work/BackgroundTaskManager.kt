package com.easypark.app.core.work

import androidx.compose.runtime.Composable

expect class BackgroundTaskManager {
    fun scheduleTestReminder()
    fun schedulePeriodicDataSync()
}

@Composable
expect fun rememberBackgroundTaskManager(): BackgroundTaskManager
