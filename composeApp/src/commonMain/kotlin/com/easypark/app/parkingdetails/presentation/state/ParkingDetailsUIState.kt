package com.easypark.app.parkingdetails.presentation.state

import com.easypark.app.parkingdetails.domain.model.ParkingDetail

data class ParkingDetailsUIState(
    val isLoading: Boolean = false,
    val parkingDetail: ParkingDetail? = null
)