package com.easypark.app.core.work

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class BackgroundTaskManager {
    actual fun scheduleTestReminder() {
        // Stub para iOS. 
    }
    
    actual fun schedulePeriodicDataSync() {
        // Stub para iOS. Tareas periódicas en iOS usan BGTaskScheduler.
    }
    
    actual fun scheduleDailyCleanup() {
        // Stub para iOS.
    }
    
    actual fun scheduleConfigWatchSync() {
        // Stub para iOS
    }

    actual fun scheduleInitialConfigSync() {
        // Stub para iOS descarga primera vez
    }
}

@Composable
actual fun rememberBackgroundTaskManager(): BackgroundTaskManager {
    return remember { BackgroundTaskManager() }
}
