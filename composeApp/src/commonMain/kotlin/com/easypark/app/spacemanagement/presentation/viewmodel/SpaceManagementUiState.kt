package com.easypark.app.spacemanagement.presentation.viewmodel

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary

data class SpaceManagementUiState(
    val isLoading: Boolean = false,
    val summary: SpaceSummary? = null,
    val parkingSpots: List<ParkingSpot> = emptyList(),
    val errorMessage: String? = null
)
