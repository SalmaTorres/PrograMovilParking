package com.easypark.app.core.data.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

actual class RemoteConfigManager actual constructor() {
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    actual suspend fun initialize() {
        // Configuración para que los cambios se vean rápido durante el desarrollo
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60) // Revisa cambios cada minuto
            .build()

        remoteConfig.setConfigSettingsAsync(settings)

        try {
            // Descarga y activa los valores de la nube
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
    actual fun getString(key: String): String = remoteConfig.getString(key)
}