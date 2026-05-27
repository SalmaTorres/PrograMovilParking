package com.easypark.app.core.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.easypark.app.MainActivity
import com.easypark.app.core.domain.session.SessionManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val sessionManager: SessionManager by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Refreshed token: $token")
        
        val currentUser = sessionManager.currentUser.value
        if (currentUser != null) {
            val sanitizedEmail = currentUser.email.replace(".", "_")
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(sanitizedEmail)
                .child("fcmToken")
                .setValue(token)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM_TOKEN", "Token actualizado exitosamente en Firebase para: $sanitizedEmail")
                    } else {
                        Log.e("FCM_TOKEN", "Error al actualizar token en Firebase", task.exception)
                    }
                }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_MSG", "From: ${message.from}")

        // Revisa si el mensaje contiene una carga útil de datos o una notificación
        val title = message.notification?.title ?: message.data["title"] ?: "EasyPark Alerta"
        val body = message.notification?.body ?: message.data["body"] ?: "Nueva notificación recibida"

        sendNotification(title, body)
    }

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_ONE_SHOT
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            pendingIntentFlags
        )

        val channelId = "default_easypark_channel"
        val iconRes = applicationInfo.icon

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = NotificationManagerCompat.from(this)
        
        try {
            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (e: SecurityException) {
            Log.e("FCM_MSG", "Error de permiso al mostrar la notificación: ${e.message}")
        }
    }
}
