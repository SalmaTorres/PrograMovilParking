package com.easypark.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easypark.app.core.permissions.PermissionType
import com.easypark.app.core.permissions.rememberPermissionManager
import com.easypark.app.core.ui.ParkBackground
import com.easypark.app.core.work.rememberBackgroundTaskManager
import com.easypark.app.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ParkBackground
        ) {
            val permissionManager = rememberPermissionManager()
            val backgroundManager = rememberBackgroundTaskManager()
            var statusText by remember { mutableStateOf("Panel de Pruebas Persona 4") }

            Box(modifier = Modifier.fillMaxSize()) {
                AppNavHost()

                // PANEL DE PRUEBAS TEMPORAL
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(statusText, color = androidx.compose.ui.graphics.Color.Black, modifier = Modifier.padding(bottom = 8.dp))
                    
                    Button(onClick = {
                        permissionManager.requestPermission(PermissionType.CAMERA) { granted ->
                            statusText = if (granted) "Permiso Cámara: OK" else "Cámara: Denegado"
                        }
                    }) { Text("Probar Cámara") }

                    Button(onClick = {
                        permissionManager.requestPermission(PermissionType.NOTIFICATIONS) { granted ->
                            if (granted) {
                                statusText = "Espera 10 seg (Notificación)"
                                backgroundManager.scheduleTestReminder()
                            } else {
                                statusText = "Notificaciones: Denegado"
                            }
                        }
                    }) { Text("Probar Notificación (10s)") }

                    Button(onClick = {
                        backgroundManager.schedulePeriodicDataSync()
                        statusText = "Sync Periódica (15min) Iniciada. ¡Revisa Logcat!"
                    }) { Text("Probar Sync (PeriodicWork)") }
                }
            }
        }
    }
}
