package com.easypark.app

import android.app.Application
import io.sentry.kotlin.multiplatform.Sentry // Si sigue rojo, es que el Sync de Gradle falló
import com.easypark.app.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        createNotificationChannel()

        // Inicialización de Sentry
        Sentry.init(this) { options ->
            options.dsn = "https://91345a8e45faad147d0d39996ade5ef0@o4511265421590528.ingest.de.sentry.io/4511265432076368"
            options.debug = true
            options.enableAutoSessionTracking = true
        }

        // Inicialización de Koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AndroidApp)
            modules(getModules())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_easypark_channel"
            val channelName = "Alertas de Parqueo"
            val descriptionText = "Notificaciones para confirmaciones de reservas y alertas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}