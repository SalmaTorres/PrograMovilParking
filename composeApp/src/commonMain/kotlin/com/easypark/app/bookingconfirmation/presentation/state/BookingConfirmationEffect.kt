package com.easypark.app.bookingconfirmation.presentation.state

sealed interface BookingConfirmationEffect {
    object NavigateBack : BookingConfirmationEffect
    object NavigateToSuccess : BookingConfirmationEffect
}