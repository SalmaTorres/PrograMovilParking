package com.easypark.app.bookingconfirmation.presentation.state

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel

data class BookingConfirmationUIState(
    val isLoading: Boolean = false,
    val bookingConfirmation: BookingConfirmationModel? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH
)

enum class PaymentMethod {
    CASH, QR
}