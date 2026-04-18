package com.easypark.app.core.domain.model

class ReservationModel (
    val id: Int,
    val spaceId: Int,
    val driverId: Int,
    val startHour: Long = 0,
    val finalHour: Long = 0,
    val totalPrice: Price,
    val state: String,
    val methodPay: String
)