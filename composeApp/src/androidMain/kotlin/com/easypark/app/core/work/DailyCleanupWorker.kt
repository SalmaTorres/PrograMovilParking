package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import kotlinx.coroutines.delay

import com.easypark.app.core.notifications.NotificationHelper
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

class DailyCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            println("DailyCleanupWorker: 🧹 Iniciando Ejercicio 13 (Limpieza Remota)...")
            
            // 1. Obtener Límite desde Firebase Remote Config
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0) // Para pruebas inmediatas
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)
            
            // Intentamos traer los valores de la nube
            remoteConfig.fetchAndActivate().await()
            
            // El docente pide que Remote Config defina el número máximo (Usaremos 3 para tus pruebas)
            val maxRecords = remoteConfig.getLong("max_reservations").let { 
                if (it == 0L) 3L else it 
            }.toInt()
            
            println("DailyCleanupWorker: El límite de registros es de $maxRecords")

            // 2. Conectar a la base de datos y contar
            val database = createDatabase(getDatabaseBuilder(applicationContext))
            val reservationDao = database.reservationDao()
            
            val currentCount = reservationDao.getReservationCount()
            println("DailyCleanupWorker: Registros actuales en Room: $currentCount")

            if (currentCount > maxRecords) {
                val toDeleteCount = currentCount - maxRecords
                println("DailyCleanupWorker: Superado el límite. Borrando $toDeleteCount registros antiguos...")
                
                // 3. Obtener los IDs que vamos a borrar para limpiar también Firebase
                val oldestReservations = reservationDao.getOldestReservationIds(toDeleteCount)
                
                // 4. Borrar en Room (Local)
                reservationDao.deleteOldestReservations(toDeleteCount)
                
                // 5. Borrar en Firebase (Nube)
                val firebaseManager = com.easypark.app.core.data.remote.FirebaseManager()
                for (id in oldestReservations) {
                    // Intentamos borrar la reserva en la ruta estándar
                    firebaseManager.saveData("reservations/$id", "null")
                    println("DailyCleanupWorker: Eliminada reserva $id de Firebase.")
                }
                
                // 6. Notificar al usuario
                NotificationHelper.showNotification(
                    applicationContext,
                    "Limpieza Completa (Celular + Nube)",
                    "Se eliminaron $toDeleteCount registros antiguos con éxito."
                )
            } else {
                println("DailyCleanupWorker: No es necesario limpiar. Todo en orden.")
            }
            
            Result.success()
        } catch (e: Exception) {
            println("DailyCleanupWorker: ❌ Error en Ejercicio 13 - ${e.message}")
            Result.failure()
        }
    }
}
