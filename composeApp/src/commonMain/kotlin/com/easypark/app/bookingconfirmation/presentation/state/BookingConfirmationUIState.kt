package com.easypark.app.bookingconfirmation.presentation.state

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation

data class BookingConfirmationUIState(
    val isLoading: Boolean = false,
    val bookingConfirmation: BookingConfirmation? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH
)

enum class PaymentMethod {
    CASH, QR
}