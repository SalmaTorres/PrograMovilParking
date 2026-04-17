package com.easypark.app.reservationhistory.presentation.state

import com.easypark.app.reservationhistory.domain.model.ReservationItem

data class ReservationHistoryUiState(
    val isLoading: Boolean = true,
    val reservations: List<ReservationItem> = emptyList(),
    val filteredReservations: List<ReservationItem> = emptyList(),
    val searchQuery: String = \"\",
    val selectedTab: Int = 0, // 0: Activas, 1: Finalizadas
    val errorMessage: String? = null
)