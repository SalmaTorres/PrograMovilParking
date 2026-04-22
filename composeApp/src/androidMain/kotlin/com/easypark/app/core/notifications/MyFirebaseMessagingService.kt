package com.easypark.app.core.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        // Si el mensaje trae una notificación (Título y Cuerpo)
        message.notification?.let {
            val title = it.title ?: "EasyPark"
            val body = it.body ?: ""
            
            // Usamos nuestro NotificationHelper para mostrarla
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Este token es el ID único del dispositivo para Firebase.
        // Lo imprimimos para que el usuario pueda copiarlo y probar en la consola.
        println("FCM TOKEN: $token")
    }
}
