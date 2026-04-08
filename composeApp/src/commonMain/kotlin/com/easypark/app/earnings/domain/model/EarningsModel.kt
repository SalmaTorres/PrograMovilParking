package com.easypark.app.earnings.domain.model

data class EarningsSummary(
    val totalEarnings: Double,
    val percentageChange: Double,
    val activeReservations: Int,
    val reservationChange: Double,
    val occupiedSpaces: Int,
    val totalSpaces: Int
)

data class EarningTransaction(
    val id: String,
    val date: String,
    val label: String,
    val amount: Double,
    val currency: String = "BS"
)
