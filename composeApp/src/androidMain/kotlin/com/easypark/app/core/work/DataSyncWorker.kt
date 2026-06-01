package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.core.notifications.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val startedAt = Clock.System.now().toEpochMilliseconds()
        val remoteConfig = RemoteConfigManager()
        val firebaseManager = FirebaseManager()
        var group = RemoteAbTestKeys.GROUP_A

        return try {
            remoteConfig.initialize()
            group = resolveGroup(remoteConfig)
            val isExperimentalGroup = group == RemoteAbTestKeys.GROUP_B

            println("DataSyncWorker: iniciando sincronizacion para grupo A/B $group.")

            val database = createDatabase(getDatabaseBuilder(applicationContext))
            val spaceDao = database.spaceDao()
            val allSpaces = spaceDao.getAllSpaces()

            val simulatedSyncDelay = if (isExperimentalGroup) 750L else 1500L
            delay(simulatedSyncDelay)

            firebaseManager.saveData(
                path = "abTestingLogs/dataSync_$startedAt",
                value = buildLogJson(
                    group = group,
                    status = "success",
                    startedAt = startedAt,
                    finishedAt = Clock.System.now().toEpochMilliseconds(),
                    localSpaces = allSpaces.size,
                    notificationType = if (isExperimentalGroup) "experimental" else "standard",
                    error = null
                )
            )

            NotificationHelper.showNotification(
                context = applicationContext,
                title = "EasyPark - Grupo $group",
                message = resolveNotificationMessage(remoteConfig, isExperimentalGroup)
            )

            println("DataSyncWorker: sincronizacion finalizada para grupo $group.")
            Result.success()
        } catch (e: Exception) {
            try {
                firebaseManager.saveData(
                    path = "abTestingLogs/dataSync_$startedAt",
                    value = buildLogJson(
                        group = group,
                        status = "failure",
                        startedAt = startedAt,
                        finishedAt = Clock.System.now().toEpochMilliseconds(),
                        localSpaces = 0,
                        notificationType = "error",
                        error = e.message
                    )
                )
            } catch (_: Exception) {
            }
            println("DataSyncWorker: error en sincronizacion A/B - ${e.message}")
            Result.failure()
        }
    }

    private fun resolveGroup(remoteConfig: RemoteConfigManager): String {
        if (!remoteConfig.getBoolean(RemoteAbTestKeys.ENABLED)) {
            return RemoteAbTestKeys.GROUP_A
        }

        val configuredGroup = remoteConfig.getString(RemoteAbTestKeys.GROUP).trim().uppercase()
        return if (configuredGroup == RemoteAbTestKeys.GROUP_B) {
            RemoteAbTestKeys.GROUP_B
        } else {
            RemoteAbTestKeys.GROUP_A
        }
    }

    private fun resolveNotificationMessage(
        remoteConfig: RemoteConfigManager,
        isExperimentalGroup: Boolean
    ): String {
        val key = if (isExperimentalGroup) {
            RemoteAbTestKeys.GROUP_B_NOTIFICATION
        } else {
            RemoteAbTestKeys.GROUP_A_NOTIFICATION
        }
        val fallback = if (isExperimentalGroup) {
            "Sincronizacion experimental completada con seguimiento detallado."
        } else {
            "Sincronizacion estandar completada."
        }

        return remoteConfig.getString(key).ifBlank { fallback }
    }

    private fun buildLogJson(
        group: String,
        status: String,
        startedAt: Long,
        finishedAt: Long,
        localSpaces: Int,
        notificationType: String,
        error: String?
    ): String {
        val sanitizedError = error?.replace("\"", "'")
        return """
            {
                "group": "$group",
                "status": "$status",
                "startedAt": $startedAt,
                "finishedAt": $finishedAt,
                "localSpaces": $localSpaces,
                "notificationType": "$notificationType",
                "error": ${if (sanitizedError == null) "null" else "\"$sanitizedError\""}
            }
        """.trimIndent()
    }
}
