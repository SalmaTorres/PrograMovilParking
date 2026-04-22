package com.easypark.app.core.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Refreshed token: $token")
        // TODO: Enviar el token a tu backend para asociarlo con el usuario actual
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Este método se llamará si recibes un mensaje cuando la app está en PRIMER PLANO.
        Log.d("FCM_MSG", "From: ${message.from}")

        // Revisa si el mensaje contiene una carga útil de datos
        if (message.data.isNotEmpty()) {
            Log.d("FCM_MSG", "Message data payload: ${message.data}")
        }

        // Revisa si el mensaje contiene una notificación
        message.notification?.let {
            Log.d("FCM_MSG", "Message Notification Body: ${it.body}")
        }
    }
}
