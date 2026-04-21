package com.easypark.app.reservationsummary.presentation.state

import com.easypark.app.core.domain.model.ReservationModel

data class ReservationSummaryUiState(
    val isLoading: Boolean = false,
    val reservation: ReservationModel? = null,
    val error: String? = null
)