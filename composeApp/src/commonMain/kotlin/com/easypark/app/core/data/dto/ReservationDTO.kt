package com.easypark.app.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDTO (
    val id: Int? = 0,
    val parkingName: String? = "",
    val address: String? = "",
    val spaceId: Int? = 0,
    val spaceNumber: Int? = 0,
    val startTime: Long? = 0L,
    val endTime: Long? = 0L,
    val totalPrice: PriceDTO? = null,
    val paymentMethod: String? = "CASH",
    val status: String? = "ACTIVE",
    val driverId: Int? = 0,
    val clientName: String? = "",
    val parkingId: Int? = 0
)