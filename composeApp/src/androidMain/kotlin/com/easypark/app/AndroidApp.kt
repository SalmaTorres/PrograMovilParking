package com.easypark.app

import android.app.Application
import com.easypark.app.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AndroidApp)
            modules(getModules()) // Carga la lista de módulos que creamos antes
        }
    }
}