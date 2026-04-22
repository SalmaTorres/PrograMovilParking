package com.easypark.app.core.sync

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.domain.repository.RemoteConfigRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
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
            
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0) // FORZAR obtención inmediata
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings).await()

            val updated = remoteConfig.fetchAndActivate().await()
            Log.d("ConfigSyncWorker", "Remote Config fetch successful. Updated: $updated")

            val repository: RemoteConfigRepository = GlobalContext.get().get()
            
            val allConfigs = remoteConfig.all
            for ((key, value) in allConfigs) {
                val newValue = value.asString()
                val oldValue = repository.getConfig(key)
                
                // Disparar push local si el valor mutó
                if (oldValue != null && oldValue != newValue) {
                    showLocalNotification(applicationContext, key, newValue)
                }

                repository.saveConfig(key, newValue)
                Log.d("ConfigSyncWorker", "Saved to Room: $key -> $newValue")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ConfigSyncWorker", "Error fetching remote config", e)
            Result.retry()
        }
    }

    private fun showLocalNotification(context: Context, key: String, newValue: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(context, "default_easypark_channel")
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle("¡Dato Remoto Cambiado!")
            .setContentText("La clave '$key' ahora es '$newValue'")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(key.hashCode(), builder.build())
        }
    }
}
