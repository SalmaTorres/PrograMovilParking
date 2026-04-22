package com.easypark.app.registerparking.data.dto

import com.easypark.app.core.data.dto.PriceDTO
import kotlinx.serialization.Serializable

@Serializable
data class ParkingDTO (
    val id: Int? = 0,
    val name: String? = "",
    val address: String? = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val pricePerHour: PriceDTO? = null,
    val rating: Float? = 0f,
    val totalSpaces: Int? = 0,
    val availableSpaces: Int? = 0,
    val schedule: String? = "08:00 - 22:00",
    val reviewCount: Int? = 0,
    val ownerId: Int? = 0,
    val totalEarnings: Double? = 0.0,
    val activeReservations: Int? = 0,
)