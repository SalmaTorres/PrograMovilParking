package com.easypark.app.core.domain.model

data class ParkingModel(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerHour: Price,
    val rating: Float? = 0.0f,
    val totalSpaces: Int,
    val schedule: String? = "08:00 - 22:00",
    val reviewCount: Int? = 0,
    val isAvailable: Boolean
)