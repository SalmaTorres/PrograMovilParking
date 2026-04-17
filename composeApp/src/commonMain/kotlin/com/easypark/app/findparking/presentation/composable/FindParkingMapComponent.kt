package com.easypark.app.findparking.presentation.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.easypark.app.findparking.presentation.state.FindParkingUIState
import com.easypark.app.core.domain.model.ParkingModel

@Composable
expect fun FindParkingMapComponent(
    state: FindParkingUIState,
    onMarkerClick: (ParkingModel) -> Unit,
    modifier: Modifier = Modifier
)