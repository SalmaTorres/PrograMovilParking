package com.easypark.app.reservationsummary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.reservationsummary.domain.usecase.GetReservationSummaryUseCase
import com.easypark.app.reservationsummary.presentation.state.ReservationSummaryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationSummaryViewModel(
    private val reservationId: Int,
    private val getReservationSummaryUseCase: GetReservationSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationSummaryUiState())
    val state = _state.asStateFlow()

    init {
        loadReservationSummary()
    }

    private fun loadReservationSummary() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val summary = getReservationSummaryUseCase(reservationId)
                _state.update { it.copy(reservation = summary, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
