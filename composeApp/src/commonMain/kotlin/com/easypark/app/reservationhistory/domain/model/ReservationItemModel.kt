package com.easypark.app.reservationhistory.domain.model

import com.easypark.app.core.domain.model.status.ReservationStatus

data class ReservationItemModel(
    val id: Int,
    val clientName: String,
    val spaceLabel: String,
    val startTime: String,
    val endTime: String,
    val status: ReservationStatus,
    val timeLeftText: String? = null
)