package com.easypark.app.bookingconfirmation.domain.model

data class BookingConfirmation(
    val locationName: String,
    val address: String,
    val spaceIdentifier: String,
    val durationHours: Int = 2,
    val pricePerHour: Double
) {
    val durationText: String get() = if (durationHours == 1) "1 Hora" else "$durationHours Horas"
    val totalCostText: String get() = "Bs ${pricePerHour * durationHours}"
}
