package com.easypark.app.bookingconfirmation.presentation.state

sealed interface BookingConfirmationEvent {
    object OnBackClick : BookingConfirmationEvent
    data class OnPaymentMethodSelected(val method: PaymentMethod) : BookingConfirmationEvent
    data class OnDurationChange(val hours: Int) : BookingConfirmationEvent
    object OnConfirmClick : BookingConfirmationEvent
}