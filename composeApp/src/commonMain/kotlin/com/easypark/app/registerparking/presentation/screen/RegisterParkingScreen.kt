package com.easypark.app.registerparking.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.navigation.NavRoute
import com.easypark.app.registerparking.presentation.composable.ParkingMapSection
import com.easypark.app.registerparking.presentation.state.RegisterParkingEffect
import com.easypark.app.registerparking.presentation.state.RegisterParkingEvent
import com.easypark.app.registerparking.presentation.viewmodel.RegisterParkingViewModel
import com.easypark.app.core.presentation.composable.*
import com.easypark.app.core.ui.*
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterParkingScreen(
    navController: NavHostController,
    viewModel: RegisterParkingViewModel = koinViewModel(),
    userFromStep1: UserModel
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initUser(userFromStep1)
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterParkingEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                RegisterParkingEffect.NavigateToSuccess -> {
                    navController.navigate(NavRoute.SpaceManagement) {
                        popUpTo(0)
                    }
                }
                is RegisterParkingEffect.ShowError -> {
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
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            ParkHeader(
                title = stringResource(Res.string.parking_registration_title),
                onBackClick = { viewModel.onEvent(RegisterParkingEvent.OnClickBack) }
            )

            Spacer(Modifier.height(12.dp))

            ParkTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnNameChanged(it)) },
                label = stringResource(Res.string.label_name),
                placeholder = stringResource(Res.string.parking_name_hint),
                isError = state.isNameError
            )

            ParkTextField(
                value = state.address,
                onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnAddressChanged(it)) },
                label = stringResource(Res.string.label_address),
                placeholder = stringResource(Res.string.parking_address_hint),
                isError = state.isAddressError
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.label_location),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ParkTextDark
            )
            Spacer(Modifier.height(8.dp))

            ParkingMapSection(
                state = state,
                onLocationChanged = { lat, lng ->
                    viewModel.onEvent(RegisterParkingEvent.OnLocationChanged(lat, lng))
                }
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    ParkTextField(
                        value = state.pricePerHour,
                        onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnPriceChanged(it)) },
                        label = stringResource(Res.string.label_price_hour),
                        placeholder = stringResource(Res.string.parking_price_hint),
                        isError = state.isPriceError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                }
                Box(Modifier.weight(1f)) {
                    ParkTextField(
                        value = state.totalSpaces,
                        onValueChange = { viewModel.onEvent(RegisterParkingEvent.OnSpacesChanged(it)) },
                        label = stringResource(Res.string.label_spaces),
                        placeholder = "25",
                        isError = state.isSpacesError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (state.isLoading) {
                ParkLoading()
            } else {
                ParkButton(
                    text = stringResource(Res.string.action_finish),
                    onClick = { viewModel.onEvent(RegisterParkingEvent.OnClickRegister) }
                )
            }
        }
    }
}
