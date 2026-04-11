package com.easypark.app.shared.domain.model

data class ParkingModel(
    val id: String,
    val name: String,
    val address: String,
    val pricePerHour: Price,
    val isAvailable: Boolean,
    val latitude: Double,
    val longitude: Double,
    val totalSpaces: Int = 0,
    val rating: Double = 0.0
)