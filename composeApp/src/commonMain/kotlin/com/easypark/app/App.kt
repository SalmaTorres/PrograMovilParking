package com.easypark.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.shared.presentation.composable.DriverFooter
import com.easypark.app.shared.presentation.composable.OwnerFooter
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkCard
import com.easypark.app.shared.presentation.composable.ParkDialog
import com.easypark.app.shared.presentation.composable.ParkHeader
import com.easypark.app.shared.presentation.composable.ParkLoading
import com.easypark.app.shared.presentation.composable.ParkTextField
import com.easypark.app.shared.ui.ParkBackground
import com.easypark.app.shared.ui.ParkGray

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentTab by remember { mutableStateOf("inicio") } // Usamos una variable para que prueben la navegación de los footers
        var isLoading by remember { mutableStateOf(false) } // Para probar el cargador
        var showDialog by remember { mutableStateOf(false) } // Para probar el diálogo

        Scaffold(
            topBar = {
                ParkHeader(
                    title = "Catálogo de Componentes",
                    onBackClick = { /* Acción volver */ },
                    onNotificationClick = { /* Acción notif */ }
                )
            },
            bottomBar = {
                Column {
                    Text("Footer Conductor:", modifier = Modifier.padding(8.dp))
                    DriverFooter(currentScreen = currentTab, onNavigate = { currentTab = it })

                    Spacer(Modifier.height(8.dp))

                    Text("Footer Dueño:", modifier = Modifier.padding(8.dp))
                    OwnerFooter(currentScreen = currentTab, onNavigate = { currentTab = it })
                }
            },
            containerColor = ParkBackground
        ) { padding ->
            if (isLoading) {
                ParkLoading()

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    isLoading = false
                }
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- SECCIÓN DE CARDS ---
                Text("Tarjetas (ParkCard)", fontWeight = FontWeight.Bold)
                ParkCard(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Esto es una ParkCard", fontWeight = FontWeight.Bold)
                    Text("Úsenla para parqueos, perfiles o historial.", color = ParkGray, fontSize = 12.sp)
                }

                Spacer(Modifier.height(24.dp))

                Text("Botones (ParkButton)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                ParkButton(text = "Botón Principal", onClick = {})
                Spacer(Modifier.height(8.dp))
                ParkButton(text = "Botón Secundario", onClick = { isLoading = true}, isSecondary = true)
                Spacer(Modifier.height(8.dp))
                ParkButton(
                    text = "Abrir Diálogo de Prueba",
                    onClick = { showDialog = true }
                )

                Spacer(Modifier.height(24.dp))

                Text("Inputs (ParkTextField)", style = MaterialTheme.typography.titleMedium)
                var textValue by remember { mutableStateOf("") }
                ParkTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = "Nombre de Usuario",
                    placeholder = "Ej: Juan Pérez",
                    // leadingImage = Res.drawable.ic_user
                )
                ParkTextField(
                    value = "",
                    onValueChange = {},
                    label = "Contraseña",
                    placeholder = "********",
                    isPassword = true
                )

                Spacer(Modifier.height(24.dp))

                Text("Paleta de Colores", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    ColorBox(Color(0xFF2D7DED), "ParkBlue")
                    ColorBox(Color(0xFFE6F0FF), "BlueLight")
                    ColorBox(Color(0xFF4CAF50), "Success")
                    ColorBox(Color(0xFFF44336), "Error")
                }

                Spacer(Modifier.height(40.dp))
                Text("⚠Nota para el equipo: Usen estos componentes para mantener el diseño uniforme.", color = Color.Gray)

                // Mostrar Diálogo si el estado es true
                if (showDialog) {
                    ParkDialog(
                        title = "Confirmación",
                        description = "¿Estás seguro de que quieres realizar esta acción en la app?",
                        onConfirm = { showDialog = false },
                        onDismiss = { showDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorBox(color: Color, name: String) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
        Box(modifier = Modifier.size(40.dp).background(color, shape = androidx.compose.foundation.shape.CircleShape))
        Text(name, fontSize = 10.sp)
    }
}