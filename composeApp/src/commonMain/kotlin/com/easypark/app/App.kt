package com.easypark.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

// Import de los recursos (ajusta el nombre si es necesario)
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra todo verticalmente
        ) {

            // --- SECCIÓN DE SENTRY ---
            Button(
                onClick = {
                    // Este es el botón para la práctica de Sentry
                    throw RuntimeException("Crash de prueba para Sentry")
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Probar Sentry (Cerrar App)")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- SECCIÓN ORIGINAL DE TU APP ---
            Button(onClick = { showContent = !showContent }) {
                Text(if (showContent) "Ocultar Logo" else "Click me!")
            }

            AnimatedVisibility(showContent) {
                // Si Greeting() te da error, puedes cambiarlo por un texto fijo
                val greeting = "Bienvenido a mi App"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")

                    // --- AQUÍ IRÍA RETROFIT Y GLIDE ---
                    Text("Aquí se mostrarán datos de Retrofit", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}