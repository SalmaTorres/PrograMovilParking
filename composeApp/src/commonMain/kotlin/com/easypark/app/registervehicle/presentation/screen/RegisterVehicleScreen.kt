package com.easypark.app.registervehicle.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easypark.app.registervehicle.presentation.state.*
import com.easypark.app.registervehicle.presentation.viewmodel.RegisterVehicleViewModel
import com.easypark.app.shared.presentation.composable.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinproject.composeapp.generated.resources.*

@Composable
fun RegisterVehicleScreen(
    viewModel: RegisterVehicleViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                RegisterVehicleEffect.NavigateBack -> onBack()
                RegisterVehicleEffect.NavigateNext -> onNext()
                is RegisterVehicleEffect.ShowError -> println(it.message)
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.register_vehicle_title),
                onBackClick = { viewModel.onEvent(RegisterVehicleEvent.OnBackClick) }
            )
        },
        bottomBar = {
            Box(Modifier.padding(16.dp)) {
                ParkButton(
                    text = stringResource(Res.string.finish),
                    onClick = {
                        viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick)
                    }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(Res.drawable.car_image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(Res.string.vehicle_details))
            Text(stringResource(Res.string.vehicle_description))

            ParkTextField(
                value = state.plate,
                onValueChange = {
                    viewModel.onEvent(RegisterVehicleEvent.OnPlateChange(it))
                },
                placeholder = stringResource(Res.string.plate_hint),
                isError = state.isPlateError
            )

            ParkTextField(
                value = state.model,
                onValueChange = {
                    viewModel.onEvent(RegisterVehicleEvent.OnModelChange(it))
                },
                placeholder = stringResource(Res.string.model_hint),
                isError = state.isModelError
            )

            ParkTextField(
                value = state.color,
                onValueChange = {
                    viewModel.onEvent(RegisterVehicleEvent.OnColorChange(it))
                },
                placeholder = stringResource(Res.string.color_hint),
                isError = state.isColorError
            )
        }
    }
}