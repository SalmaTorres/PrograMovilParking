package com.easypark.app.parkingdetails.domain.model

data class ParkingDetail(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val address: String,
    val pricePerHour: String,
    val schedule: String,
    val isAvailable: Boolean,
    val imageUrl: String? = null
)
