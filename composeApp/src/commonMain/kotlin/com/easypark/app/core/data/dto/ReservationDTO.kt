package com.easypark.app.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDTO (
    @Serializable(with = SafeIntSerializer::class) val id: Int? = 0,
    val parkingName: String? = "",
    val address: String? = "",
    @Serializable(with = SafeIntSerializer::class) val spaceId: Int? = 0,
    @Serializable(with = SafeIntSerializer::class) val spaceNumber: Int? = 0,
    @Serializable(with = SafeLongSerializer::class) val startTime: Long? = 0L,
    @Serializable(with = SafeLongSerializer::class) val endTime: Long? = 0L,
    val totalPrice: PriceDTO? = null,
    val paymentMethod: String? = "CASH",
    val status: String? = "ACTIVE",
    @Serializable(with = SafeIntSerializer::class) val driverId: Int? = 0,
    val clientName: String? = "",
    @Serializable(with = SafeIntSerializer::class) val parkingId: Int? = 0,
    val vehiclePlate: String? = "",
    val vehicleType: String? = "",
    @Serializable(with = SafeLongSerializer::class) val arrivalTime: Long? = 0L,
    @Serializable(with = SafeBooleanSerializer::class) val warned5Min: Boolean? = false
)