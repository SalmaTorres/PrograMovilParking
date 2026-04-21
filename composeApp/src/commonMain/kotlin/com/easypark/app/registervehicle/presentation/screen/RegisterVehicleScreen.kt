package com.easypark.app.registervehicle.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.navigation.NavRoute
import com.easypark.app.registervehicle.presentation.state.*
import com.easypark.app.registervehicle.presentation.viewmodel.RegisterVehicleViewModel
import com.easypark.app.core.presentation.composable.*
import com.easypark.app.core.ui.ParkGray
import com.easypark.app.core.ui.ParkTextDark
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinproject.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterVehicleScreen(
    navController: NavHostController,
    viewModel: RegisterVehicleViewModel = koinViewModel(),
    userFromStep1: UserModel
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initUser(userFromStep1)
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterVehicleEffect.NavigateBack -> navController.popBackStack()
                RegisterVehicleEffect.NavigateNext -> {
                    navController.navigate(NavRoute.FindParking) {
                        popUpTo(0)
                    }
                }
                is RegisterVehicleEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding().imePadding()
            ) 
        },
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.vehicle_registration_title),
                onBackClick = { viewModel.onEvent(RegisterVehicleEvent.OnBackClick) }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                ParkButton(
                    text = stringResource(Res.string.action_finish),
                    onClick = { viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(Res.drawable.car_image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.parking_details_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ParkTextDark
            )
            Text(
                text = stringResource(Res.string.parking_description),
                fontSize = 14.sp,
                color = ParkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkTextField(
                value = state.plate,
                onValueChange = { viewModel.onEvent(RegisterVehicleEvent.OnPlateChange(it)) },
                label = stringResource(Res.string.label_plate),
                placeholder = stringResource(Res.string.vehicle_plate_hint),
                isError = state.isPlateError
            )

            ParkTextField(
                value = state.model,
                onValueChange = { viewModel.onEvent(RegisterVehicleEvent.OnModelChange(it)) },
                label = stringResource(Res.string.label_model),
                placeholder = stringResource(Res.string.vehicle_model_hint),
                isError = state.isModelError
            )

            ParkTextField(
                value = state.color,
                onValueChange = { viewModel.onEvent(RegisterVehicleEvent.OnColorChange(it)) },
                label = stringResource(Res.string.label_color),
                placeholder = stringResource(Res.string.vehicle_color_hint),
                isError = state.isColorError
            )
        }
    }
}