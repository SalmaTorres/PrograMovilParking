package com.easypark.app.findparking.presentation.state

import com.easypark.app.registerparking.domain.model.ParkingModel

data class FindParkingUIState(
    val searchQuery: String = "",
    val allParkings: List<ParkingModel> = emptyList(),
    val suggestions: List<ParkingModel> = emptyList(),
    val selectedParking: ParkingModel? = null,
    val isLoading: Boolean = false,
    val mapCenter: Pair<Double, Double> = Pair(-17.3935, -66.1570)
)