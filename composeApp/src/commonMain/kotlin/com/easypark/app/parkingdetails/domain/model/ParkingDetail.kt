package com.easypark.app.parkingdetails.domain.model

import com.easypark.app.core.domain.model.Price

data class ParkingDetail(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val address: String,
    val pricePerHour: Price,
    val schedule: String,
    val isAvailable: Boolean,
    val imageUrl: String? = null
)
