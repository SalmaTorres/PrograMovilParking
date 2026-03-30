package com.easypark.app.registerparking.presentation.screen

import RegisterParkingViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.registerparking.presentation.composable.ParkingMapSection
import com.easypark.app.registerparking.presentation.state.RegisterParkingEvent
import com.easypark.app.shared.presentation.composable.*
import com.easypark.app.shared.ui.*

@Composable
fun RegisterParkingScreen(
    viewModel: RegisterParkingViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Escuchar efectos (Navegación)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterParkingEffect.NavigateBack -> onNavigateBack()
                RegisterParkingEffect.NavigateToSuccess -> onSuccess()
                is RegisterParkingEffect.ShowToast -> { /* Mostrar snackbar o toast */ }
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = "Registra tu parqueo",
                onBackClick = { viewModel.onEvent(RegisterParkingEvent.OnClickBack) }
            )
        },
        containerColor = ParkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            ParkTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnNameChanged(it)) },
                label = "Nombre del parqueo",
                placeholder = "e.g. Downtown Central Garage",
                isError = state.isNameError
            )

            ParkTextField(
                value = state.address,
                onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnAddressChanged(it)) },
                label = "Direccion",
                placeholder = "Enter street address",
                isError = state.isAddressError
            )

            Spacer(Modifier.height(8.dp))
            Text("Localizacion", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ParkTextDark)
            Spacer(Modifier.height(8.dp))

            // --- AQUÍ USAMOS TU COMPONENTE ---
            ParkingMapSection(
                state = state,
                onLocationChanged = { lat, lng ->
                    viewModel.onEvent(RegisterParkingEvent.OnLocationChanged(lat, lng))
                }
            )
            // --------------------------------

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    ParkTextField(
                        value = state.pricePerHour,
                        onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnPriceChanged(it)) },
                        label = "Precio por hora",
                        placeholder = "$ 5.00",
                        isError = state.isPriceError
                    )
                }
                Box(Modifier.weight(1f)) {
                    ParkTextField(
                        value = state.totalSpaces,
                        onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnSpacesChanged(it)) },
                        label = "Cant. espacios",
                        placeholder = "25",
                        isError = state.isSpacesError
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (state.isLoading) {
                ParkLoading()
            } else {
                ParkButton(
                    text = "Finalizar",
                    onClick = { viewModel.onEvent(RegisterParkingEvent.OnClickRegister) }
                )
            }

            Text(
                text = "By clicking Finish setup, you agree to our terms of service for parking operators.",
                fontSize = 12.sp,
                color = ParkGray,
                modifier = Modifier.padding(vertical = 20.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}