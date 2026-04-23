package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.domain.repository.RemoteConfigRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val repository: RemoteConfigRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0) // Cache por defecto 0s para dev
                .build()
            remoteConfig.setConfigSettingsAsync(settings)

            println("ConfigSyncWorker: Extrayendo configuración remota de Firebase...")
            remoteConfig.fetchAndActivate().await()

            // Itera sobre TODAS las llaves descargadas
            remoteConfig.all.forEach { (key, remoteValue) ->
                repository.saveConfig(key, remoteValue.asString())
            }

            println("ConfigSyncWorker: Descarga completa en base de datos local.")
            Result.success()
        } catch (e: Exception) {
            println("ConfigSyncWorker error: ${e.message}")
            Result.retry()
        }
    }
}
