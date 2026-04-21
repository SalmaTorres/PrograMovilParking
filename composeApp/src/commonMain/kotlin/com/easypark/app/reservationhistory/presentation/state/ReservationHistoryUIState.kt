package com.easypark.app.reservationhistory.presentation.state

import com.easypark.app.reservationhistory.domain.model.ReservationItemModel

data class ReservationHistoryUiState(
    val isLoading: Boolean = true,
    val reservations: List<ReservationItemModel> = emptyList(),
    val filteredReservations: List<ReservationItemModel> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0, // 0: Activas, 1: Finalizadas
    val errorMessage: String? = null
)