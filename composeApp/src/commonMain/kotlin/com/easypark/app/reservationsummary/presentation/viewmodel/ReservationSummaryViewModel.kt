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
    val sessionManager: SessionManager,
    private val getReservationSummaryUseCase: GetReservationSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationSummaryUiState())
    val state = _state.asStateFlow()

    init {
        startRealtimeObservation()
        runAutoEvacuation()
    }

    private fun runAutoEvacuation() {
        viewModelScope.launch {
            try {
                getReservationSummaryUseCase.evacuate()
            } catch (e: Exception) {
                println("EVACUATION_ERROR: ${e.message}")
            }
        }
    }

    fun checkIn(reservationId: Int, parkingId: Int, spaceId: Int) {
        viewModelScope.launch {
            try {
                getReservationSummaryUseCase.checkIn(reservationId, parkingId, spaceId)
            } catch (e: Exception) {
                println("CheckIn Error: ${e.message}")
            }
        }
    }

    fun checkOut(reservationId: Int, parkingId: Int, spaceId: Int) {
        viewModelScope.launch {
            try {
                getReservationSummaryUseCase.checkOut(reservationId, parkingId, spaceId)
            } catch (e: Exception) {
                println("CheckOut Error: ${e.message}")
            }
        }
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

    fun autoCancelReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
        viewModelScope.launch {
            try {
                // Aquí llamamos a una función que marque como CANCELADO y libere el espacio
                getReservationSummaryUseCase.cancelExpiredReservation(reservationId, parkingId, spaceId)
                println("LOG: Reserva $reservationId cancelada por tiempo expirado")
            } catch (e: Exception) {
                println("Error al cancelar: ${e.message}")
            }
        }
    }
}