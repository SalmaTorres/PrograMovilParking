package com.easypark.app.parkingdetails.presentation.state

import com.easypark.app.core.domain.model.ParkingModel

data class ParkingDetailsUIState(
    val parkingDetail: ParkingModel? = null,
    val isLoading: Boolean = false,
    val userRating: Float = 0f
)