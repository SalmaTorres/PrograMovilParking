package com.easypark.app.bookingconfirmation.domain.model

data class BookingConfirmation(
    val locationName: String,
    val address: String,
    val spaceIdentifier: String,
    val durationText: String,
    val totalCostText: String
)
