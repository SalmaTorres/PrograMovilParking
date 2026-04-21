package com.easypark.app.bookingconfirmation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.bookingconfirmation.domain.usecase.ConfirmReservationUseCase
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingInfoUseCase
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEffect
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationUIState
import com.easypark.app.core.domain.session.SessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingConfirmationViewModel(
    private val parkingId: Int,
    private val getBookingInfoUseCase: GetBookingInfoUseCase,
    private val confirmReservationUseCase: ConfirmReservationUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(BookingConfirmationUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BookingConfirmationEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val info = getBookingInfoUseCase(parkingId)
            _state.update { it.copy(isLoading = false, bookingConfirmation = info) }
        }
    }

    fun onEvent(event: BookingConfirmationEvent) {
        when (event) {
            is BookingConfirmationEvent.OnPaymentMethodSelected -> {
                _state.update { it.copy(selectedPaymentMethod = event.method) }
            }
            is BookingConfirmationEvent.OnDurationChange -> {
                _state.update { s ->
                    val newBooking = s.bookingConfirmation?.copy(
                        durationHours = event.hours,
                        totalCost = s.bookingConfirmation.pricePerHour * event.hours
                    )
                    s.copy(bookingConfirmation = newBooking)
                }
            }
            BookingConfirmationEvent.OnBackClick -> emit(BookingConfirmationEffect.NavigateBack)
            is BookingConfirmationEvent.OnConfirmClick -> confirm()
        }
    }

    private fun confirm() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentUserId = sessionManager.getUserId()
            val duration = _state.value.bookingConfirmation?.durationHours ?: 1

            if (currentUserId != -1) {
                val reservationId = confirmReservationUseCase(parkingId, currentUserId, duration)

                if (reservationId != null) {
                    emit(BookingConfirmationEffect.NavigateToSuccess(reservationId))
                } else {
                    _state.update { it.copy(isLoading = false) }
                    emit(BookingConfirmationEffect.ShowError("Error al procesar la reserva"))
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                emit(BookingConfirmationEffect.ShowError("Sesión expirada. Inicia sesión de nuevo."))
            }
        }
    }

    private fun emit(effect: BookingConfirmationEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}