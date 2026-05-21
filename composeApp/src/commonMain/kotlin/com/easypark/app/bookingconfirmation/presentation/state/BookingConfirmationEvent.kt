package com.easypark.app.bookingconfirmation.presentation.state

sealed interface BookingConfirmationEvent {
    object OnBackClick : BookingConfirmationEvent
    data class OnPaymentMethodSelected(val method: PaymentMethod) : BookingConfirmationEvent
    data class OnDurationChange(val hours: Int) : BookingConfirmationEvent
    data class OnVehiclePlateChange(val plate: String) : BookingConfirmationEvent
    data class OnVehicleTypeChange(val type: String) : BookingConfirmationEvent
    object OnConfirmClick : BookingConfirmationEvent
}