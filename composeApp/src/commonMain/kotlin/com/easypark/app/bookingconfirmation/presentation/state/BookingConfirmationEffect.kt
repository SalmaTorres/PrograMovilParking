package com.easypark.app.bookingconfirmation.presentation.state

sealed interface BookingConfirmationEffect {
    object NavigateBack : BookingConfirmationEffect
    data class NavigateToSuccess(val reservationId: Int) : BookingConfirmationEffect
    data class ShowError(val message: String) : BookingConfirmationEffect
}