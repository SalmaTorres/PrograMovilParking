package com.easypark.app.reservationsummary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.reservationsummary.domain.usecase.GetReservationSummaryUseCase
import com.easypark.app.reservationsummary.presentation.state.ReservationSummaryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationSummaryViewModel(
    private val sessionManager: SessionManager,
    private val getReservationSummaryUseCase: GetReservationSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationSummaryUiState())
    val state = _state.asStateFlow()

    init {
        startRealtimeObservation()
    }

    private fun startRealtimeObservation() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getReservationSummaryUseCase.observe(userId).collect { liveList ->
                _state.update { it.copy(
                    reservations = liveList,
                    isLoading = false,
                    error = null
                )}
                println("LOG: Lista de reservas actualizada desde Firebase")
            }
        }
    }
}