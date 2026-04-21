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
        loadActiveReservations()
    }

    private fun loadActiveReservations() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            _state.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    val list = getReservationSummaryUseCase(userId)
                    _state.update { it.copy(reservations = list, isLoading = false) }
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
            }
        }
    }
}