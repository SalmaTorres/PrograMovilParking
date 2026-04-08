package com.easypark.app.reservationhistory.domain.model

enum class ReservationStatus {
    ACTIVE,
    ENDING_SOON,
    FINISHED
}

data class ReservationItem(
    val id: String,
    val clientName: String,
    val spaceLabel: String,
    val startTime: String,
    val endTime: String,
    val status: ReservationStatus,
    val timeLeftText: String? = null
)
