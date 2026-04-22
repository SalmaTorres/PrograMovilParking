package com.easypark.app.earnings.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EarningsSummaryDTO(
    val totalEarnings: Double? = 0.0,
    val activeReservations: Int? = 0,
    val occupiedSpaces: Int? = 0,
    val totalSpaces: Int? = 20
)