package com.easypark.app.reservationhistory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.reservationhistory.domain.usecase.GetReservationHistoryUseCase
import com.easypark.app.reservationhistory.presentation.state.ReservationHistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationHistoryViewModel(
    private val sessionManager: SessionManager,
    private val getReservationHistoryUseCase: GetReservationHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationHistoryUiState())
    val state = _state.asStateFlow()

    init {
        loadReservations()
    }

    private fun loadReservations() {
        viewModelScope.launch {
            val parkingId = sessionManager.currentParkingId ?: return@launch

            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val list = getReservationHistoryUseCase(parkingId)

                _state.update { it.copy(isLoading = false, reservations = list) }
                applyFilters()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
        applyFilters()
    }

    private fun applyFilters() {
        _state.update { currentState ->
            val filtered = currentState.reservations.filter { reservation ->
                val matchesTab = if (currentState.selectedTab == 0) {
                    reservation.status == ReservationStatus.ACTIVE ||
                    reservation.status == ReservationStatus.ENDING_SOON
                } else {
                    reservation.status == ReservationStatus.FINISHED
                }
                val matchesSearch = if (currentState.searchQuery.isBlank()) {
                    true
                } else {
                    reservation.clientName.contains(currentState.searchQuery, ignoreCase = true)
                }
                
                matchesTab && matchesSearch
            }
            currentState.copy(filteredReservations = filtered)
        }
    }
}