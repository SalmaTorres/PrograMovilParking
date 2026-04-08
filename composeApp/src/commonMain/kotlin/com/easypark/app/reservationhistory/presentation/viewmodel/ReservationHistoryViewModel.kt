package com.easypark.app.reservationhistory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.reservationhistory.domain.model.ReservationStatus
import com.easypark.app.reservationhistory.domain.usecase.GetReservationHistoryUseCase
import com.easypark.app.reservationhistory.presentation.state.ReservationHistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ReservationHistoryViewModel(
    private val getReservationHistoryUseCase: GetReservationHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationHistoryUiState())
    val state: StateFlow<ReservationHistoryUiState> = _state.asStateFlow()

    init {
        loadReservations()
    }

    private fun loadReservations() {
        getReservationHistoryUseCase()
            .onEach { list ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    reservations = list
                )
            }
            .catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun getFilteredReservations() = _state.value.reservations.filter { reservation ->
        val matchesTab = if (_state.value.selectedTab == 0) {
            reservation.status == ReservationStatus.ACTIVE || reservation.status == ReservationStatus.ENDING_SOON
        } else {
            reservation.status == ReservationStatus.FINISHED
        }
        val matchesSearch = reservation.clientName.contains(_state.value.searchQuery, ignoreCase = true)
        
        matchesTab && matchesSearch
    }
}