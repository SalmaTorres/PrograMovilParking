package com.easypark.app.parkingdetails.presentation.state

import com.easypark.app.registerparking.domain.model.ParkingModel

data class ParkingDetailsUIState(
    val parkingDetail: ParkingModel? = null,
    val isLoading: Boolean = false,
    val userRating: Int = 0,
    val errorMessage: String? = null
)