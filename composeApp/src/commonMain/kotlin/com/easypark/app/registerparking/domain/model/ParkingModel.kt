package com.easypark.app.registerparking.domain.model

data class ParkingModel(
    val id: String = "",
    val ownerId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerHour: Double,
    val totalSpaces: Int,
    val rating: Double = 0.0
)