package com.easypark.app.reservationsummary.presentation.state

import ReservationModel

data class ReservationSummaryUiState(
    val isLoading: Boolean = false,
    val reservations: List<ReservationModel> = emptyList(),
    val error: String? = null
)