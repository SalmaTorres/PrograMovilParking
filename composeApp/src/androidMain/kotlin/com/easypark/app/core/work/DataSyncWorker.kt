package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Simulamos un proceso de limpieza de base de datos o sincronización
            // de reservas que toma un par de segundos.
            println("DataSyncWorker: Iniciando limpieza periódica de reservas antiguas...")
            delay(2000)
            println("DataSyncWorker: Limpieza finalizada con éxito.")
            
            Result.success()
        } catch (e: Exception) {
            println("DataSyncWorker: Error durante la limpieza - ${e.message}")
            Result.failure()
        }
    }
}
