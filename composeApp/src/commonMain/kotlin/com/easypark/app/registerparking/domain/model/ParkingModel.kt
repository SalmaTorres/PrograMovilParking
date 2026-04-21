package com.easypark.app.registerparking.domain.model

import com.easypark.app.core.domain.model.PriceModel

data class ParkingModel(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerHour: PriceModel,
    val rating: Float? = 0.0f,
    val totalSpaces: Int = 0,
    val schedule: String? = "08:00 - 22:00",
    val reviewCount: Int? = 0,
    val availableSpaces: Int = 0,
    val isAvailable: Boolean = availableSpaces > 0,
    val ownerId: Int
)