package com.easypark.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ReservationModel(
    val id: Int = 0,
    val parkingName: String,
    val address: String,
    val spaceId: Int = 0,
    val spaceNumber: Int,
    val startTime: Long,
    val endTime: Long,
    val totalPrice: PriceModel,
    val paymentMethod: String = "CASH",
    val status: String = "ACTIVE"
) {
    val entryTime: String get() = "10:30 AM"

    val durationText: String get() {
        val diff = endTime - startTime
        val hours = diff / 3600000
        return if (hours <= 1) "1 hora" else "$hours horas"
    }
}