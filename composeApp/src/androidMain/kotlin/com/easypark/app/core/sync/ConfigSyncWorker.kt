package com.easypark.app.core.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.domain.repository.RemoteConfigRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import org.koin.core.context.GlobalContext

class ConfigSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("ConfigSyncWorker", "Starting Remote Config Sync...")
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            
            // Set minimum fetch interval for development if needed, but we'll use default.
            val updated = remoteConfig.fetchAndActivate().await()
            Log.d("ConfigSyncWorker", "Remote Config fetch successful. Updated: $updated")

            val repository: RemoteConfigRepository = GlobalContext.get().get()
            
            val allConfigs = remoteConfig.all
            for ((key, value) in allConfigs) {
                repository.saveConfig(key, value.asString())
                Log.d("ConfigSyncWorker", "Saved to Room: $key -> ${value.asString()}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ConfigSyncWorker", "Error fetching remote config", e)
            // Retry if it's a network issue
            Result.retry()
        }
    }
}
