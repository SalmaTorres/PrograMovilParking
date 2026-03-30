package com.easypark.app.bookingconfirmation.presentation.state

sealed interface BookingConfirmationEvent {
    object OnBackClick : BookingConfirmationEvent
    data class OnPaymentMethodSelected(val method: PaymentMethod) : BookingConfirmationEvent
    object OnConfirmClick : BookingConfirmationEvent
}