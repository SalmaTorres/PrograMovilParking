package com.easypark.app.earnings.domain.model

data class EarningsSummaryModel(
    val totalEarnings: Double,
    val percentageChange: Double,
    val activeReservations: Int,
    val reservationChange: Int,
    val occupiedSpaces: Int,
    val totalSpaces: Int,
    val isCapacityLimited: Boolean
)