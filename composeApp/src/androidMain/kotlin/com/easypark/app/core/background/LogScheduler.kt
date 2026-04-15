package com.easypark.app.core.background

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class LogScheduler(private val context: Context) {

    companion object {
        private const val LOG_WORKNAME = "logUploadWork"
        private const val INTERVAL_MINUTES = 15L // Mínimo permitido por Android
    }

    fun schedulePeriodicLogUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val logRequest = PeriodicWorkRequest.Builder(
            LogUploadWorker::class.java,
            INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            LOG_WORKNAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantiene la tarea existente si ya está encolada
            logRequest
        )
    }
}
