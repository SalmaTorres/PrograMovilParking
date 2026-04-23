package com.easypark.app.core.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.AppDatabase
import com.easypark.app.core.data.remote.FirebaseManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class OfflineSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result {
        val database: AppDatabase = get()
        val firebaseManager: FirebaseManager = get()
        val dao = database.bookingConfirmationDao()

        // 1. Obtener reservas no sincronizadas
        val pending = dao.getUnsyncedReservations()
        if (pending.isEmpty()) return Result.success()

        return try {
            pending.forEach { res ->
                // 2. Subir a Firebase (Formato simplificado para el respaldo)
                val firebaseData = """
                {
                    "id": ${res.id},
                    "status": "${res.state}",
                    "driverId": ${res.driverId},
                    "totalPrice": { "amount": ${res.totalPrice}, "currency": "BOB" }
                }
                """.trimIndent()

                firebaseManager.saveData("reservations/${res.id}", firebaseData)

                // 3. Marcar como sincronizado en Room
                dao.markAsSynced(res.id)
            }

            // 4. Mostrar Notificación de éxito (Requisito del ejercicio)
            showSyncNotification(pending.size)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showSyncNotification(count: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "offline_sync"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sincronización Cloud", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("EasyPark Sincronizado")
            .setContentText("Se han respaldado $count reservas en la nube correctamente.")
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setAutoCancel(true)
            .build()

        manager.notify(100, notification)
    }
}