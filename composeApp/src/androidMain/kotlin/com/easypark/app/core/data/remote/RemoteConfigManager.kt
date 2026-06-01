package com.easypark.app.core.data.remote

import com.easypark.app.core.work.RemoteAbTestKeys
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

actual class RemoteConfigManager actual constructor() {
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    actual suspend fun initialize() {
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()

        remoteConfig.setConfigSettingsAsync(settings).await()
        remoteConfig.setDefaultsAsync(
            mapOf(
                "app_mantenimiento" to false,
                "mensaje_mantenimiento" to "",
                RemoteAbTestKeys.ENABLED to true,
                RemoteAbTestKeys.GROUP to RemoteAbTestKeys.GROUP_A,
                RemoteAbTestKeys.GROUP_A_FREQUENCY_MINUTES to RemoteAbTestKeys.DEFAULT_GROUP_A_FREQUENCY_MINUTES.toString(),
                RemoteAbTestKeys.GROUP_B_FREQUENCY_MINUTES to RemoteAbTestKeys.DEFAULT_GROUP_B_FREQUENCY_MINUTES.toString(),
                RemoteAbTestKeys.GROUP_A_NOTIFICATION to "Sincronizacion estandar completada.",
                RemoteAbTestKeys.GROUP_B_NOTIFICATION to "Sincronizacion experimental completada con seguimiento detallado."
            )
        ).await()

        try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
    actual fun getString(key: String): String = remoteConfig.getString(key)
}
