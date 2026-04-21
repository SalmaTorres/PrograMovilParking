package com.easypark.app.bookingconfirmation.domain.model

import com.easypark.app.core.domain.model.status.Currency
import com.easypark.app.core.domain.model.PriceModel

data class BookingConfirmationModel(
    val parkingId: Int,
    val locationName: String,
    val address: String,
    val pricePerHour: Double,
    val durationHours: Int = 1,
    val totalCost: Double = 0.0,
    val spaceIdentifier: String = "Asignación automática"
) {
    val totalCostText: PriceModel get() = PriceModel(totalCost, Currency.BOB)
}