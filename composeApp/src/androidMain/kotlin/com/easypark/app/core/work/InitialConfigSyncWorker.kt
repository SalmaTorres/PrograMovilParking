package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import com.easypark.app.core.data.entity.RemoteConfigEntity
import com.easypark.app.core.data.remote.RemoteConfigManager

class InitialConfigSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            println("InitialConfigSyncWorker: Descargando sincronización inicial de Firebase...")

            // Get Remote Config directly
            val remoteConfigManager = RemoteConfigManager()
            remoteConfigManager.initialize() // Esto forzará el Fetch y Activate en la red
            
            val mantenimientoState = remoteConfigManager.getBoolean("app_mantenimiento").toString()
            val mantenimientoMsg = remoteConfigManager.getString("mensaje_mantenimiento")

            val database = createDatabase(getDatabaseBuilder(applicationContext))
            val dao = database.remoteConfigDao()
            
            // Reemplazamos los valores iniciales para que la UI los pinte de Room instantaneamente
            dao.insertConfig(RemoteConfigEntity("app_mantenimiento", mantenimientoState))
            dao.insertConfig(RemoteConfigEntity("mensaje_mantenimiento", mantenimientoMsg))

            println("InitialConfigSyncWorker: Sincronización Inicial finalizada con éxito.")
            Result.success()
        } catch (e: Exception) {
            println("InitialConfigSyncWorker: Fallo en red/Room: ${e.message}")
            Result.retry()
        }
    }
}
