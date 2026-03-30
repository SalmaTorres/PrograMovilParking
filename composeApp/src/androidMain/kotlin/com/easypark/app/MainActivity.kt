package com.easypark.app

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Diseño de borde a borde (opcional pero recomendado)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 2. CONFIGURACIÓN DE OPENSTREETMAP (Muy importante)
        // Se hace ANTES de setContent para que el mapa cargue bien
        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        // Identifica tu app ante los servidores de mapas (User Agent)
        Configuration.getInstance().userAgentValue = "com.easypark.app.v1"

        // 3. CARGAR LA INTERFAZ (Solo una vez)
        setContent {
            App()
        }
    }
}