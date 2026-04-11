package com.easypark.app.bookingconfirmation.domain.model

import com.easypark.app.shared.domain.model.Price

data class BookingConfirmation(
    val locationName: String,
    val address: String,
    val spaceIdentifier: String,
    val durationHours: Int = 2,
    val pricePerHour: Price
) {
    val totalCostText: Price get() = Price(
        amount = pricePerHour.amount * durationHours,
        currency = pricePerHour.currency
    )
}
