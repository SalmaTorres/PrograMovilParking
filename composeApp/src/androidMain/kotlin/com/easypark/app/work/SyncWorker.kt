package com.easypark.app.work

import android.content.Context
import com.easypark.app.findparking.domain.usecase.GetParkingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

    // Inyectando el caso de uso directamente desde commonMain usando Koin
    private val getParkingsUseCase: GetParkingsUseCase by inject()

    override suspend fun doWork(): Result {
        println("Ejecutando instrucción para sincronizar parqueos...")
        
        return try {
            // Ejecutamos el caso de uso
            val parkings = getParkingsUseCase.invoke()
            println("Datos sincronizados: ${parkings.size} parqueos obtenidos.")
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
