package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder

class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            println("DataSyncWorker: Iniciando sincronización de datos de fondo...")
            
            // 1. Conectamos con la base de datos real que implementó Persona 1
            val database = createDatabase(getDatabaseBuilder(applicationContext))
            val spaceDao = database.spaceDao()
            
            // 2. Simulamos la tarea comprobando datos (por ejemplo, contar parqueos)
            val allSpaces = spaceDao.getAllSpaces()
            println("DataSyncWorker: ¡Conexión Exitosa a DB! Encontramos ${allSpaces.size} espacios guardados localmente.")
            
            // 3. Simular tiempo de subida a la nube
            delay(1500)
            println("DataSyncWorker: Sincronización finalizada con éxito.")
            
            Result.success()
        } catch (e: Exception) {
            println("DataSyncWorker: Error conectando a la DB - ${e.message}")
            Result.failure()
        }
    }
}
