package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.repository.AppEventRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppEventSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val repository: AppEventRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            repository.syncEvents()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
