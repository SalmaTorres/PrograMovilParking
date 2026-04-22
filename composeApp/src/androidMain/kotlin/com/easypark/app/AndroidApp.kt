package com.easypark.app

import android.app.Application
import io.sentry.kotlin.multiplatform.Sentry // Si sigue rojo, es que el Sync de Gradle falló
import com.easypark.app.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicialización de Sentry
        Sentry.init(this) { options ->
            // REEMPLAZA ESTO CON TU DSN REAL DE LA WEB DE SENTRY
            options.dsn = "https://91345a8e45faad147d0d39996ade5ef0@o4511265421590528.ingest.de.sentry.io/4511265432076368"
            options.debug = true
            options.enableAutoSessionTracking = true
        }

        // Inicialización de Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AndroidApp)
            modules(getModules())
        }
    }
}