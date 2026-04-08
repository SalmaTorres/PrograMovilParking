package com.easypark.app.bookingconfirmation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingConfirmationUseCase
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEffect
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingConfirmationViewModel(
    private val parkingId: String,
    private val getBookingConfirmationUseCase: GetBookingConfirmationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BookingConfirmationUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BookingConfirmationEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadBookingConfirmation()
    }

    private fun loadBookingConfirmation() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val detail = getBookingConfirmationUseCase(parkingId)
            _state.update { it.copy(isLoading = false, bookingConfirmation = detail) }
        }
    }

    fun onEvent(event: BookingConfirmationEvent) {
        when (event) {
            is BookingConfirmationEvent.OnPaymentMethodSelected -> {
                _state.update { it.copy(selectedPaymentMethod = event.method) }
            }
            is BookingConfirmationEvent.OnDurationChange -> {
                _state.update { 
                    val updatedBooking = it.bookingConfirmation?.copy(durationHours = event.hours)
                    it.copy(bookingConfirmation = updatedBooking) 
                }
            }
            BookingConfirmationEvent.OnBackClick -> emit(BookingConfirmationEffect.NavigateBack)
            BookingConfirmationEvent.OnConfirmClick -> {
                emit(BookingConfirmationEffect.NavigateToSuccess)
            }
        }
    }

    private fun emit(effect: BookingConfirmationEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}