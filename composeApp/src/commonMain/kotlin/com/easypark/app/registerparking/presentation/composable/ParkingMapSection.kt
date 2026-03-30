package com.easypark.app.registerparking.presentation.composable

import androidx.compose.runtime.Composable
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState

@Composable
expect fun ParkingMapSection(
    state: RegisterParkingUIState,
    onLocationChanged: (Double, Double) -> Unit
)