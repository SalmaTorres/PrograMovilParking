package com.easypark.app.registervehicle.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.easypark.app.registervehicle.presentation.state.*
import com.easypark.app.registervehicle.presentation.viewmodel.RegisterVehicleViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.car_image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterVehicleScreen(
    viewModel: RegisterVehicleViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterVehicleEffect.NavigateBack -> onBack()
                RegisterVehicleEffect.NavigateNext -> onNext()
                is RegisterVehicleEffect.ShowError -> println(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registra tu vehiculo") }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Finalizar")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3F2FD)) // azul claro
            ) {

                Image(
                    painter = painterResource(Res.drawable.car_image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp) // separa la imagen del borde
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Vehicle Details", style = MaterialTheme.typography.titleLarge)
            Text("Please provide your vehicle information to get started.")

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = state.plate,
                onValueChange = {
                    viewModel.onEvent(RegisterVehicleEvent.OnPlateChange(it))
                },
                placeholder = { Text("e.g. ABC-1234") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = state.model,
                onValueChange = {
                    viewModel.onEvent(RegisterVehicleEvent.OnModelChange(it))
                },
                placeholder = { Text("e.g. Toyota Camry 2024") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = state.color,
                onValueChange = {
                    viewModel.onEvent(RegisterVehicleEvent.OnColorChange(it))
                },
                placeholder = { Text("Select vehicle color") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}