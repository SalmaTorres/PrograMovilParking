package com.easypark.app.bookingconfirmation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingConfirmationUseCase
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationUIState
import com.easypark.app.bookingconfirmation.presentation.state.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingConfirmationViewModel(
    private val getBookingConfirmationUseCase: GetBookingConfirmationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BookingConfirmationUIState())
    val state = _state.asStateFlow()

    init {
        loadBookingConfirmation("1")
    }

    private fun loadBookingConfirmation(id: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val detail = getBookingConfirmationUseCase(id)
            _state.update {
                it.copy(isLoading = false, bookingConfirmation = detail)
            }
        }
    }

    fun onEvent(event: BookingConfirmationEvent) {
        when (event) {
            is BookingConfirmationEvent.OnPaymentMethodSelected -> {
                _state.update { it.copy(selectedPaymentMethod = event.method) }
            }
            BookingConfirmationEvent.OnBackClick -> {}
            BookingConfirmationEvent.OnConfirmClick -> {}
        }
    }
}