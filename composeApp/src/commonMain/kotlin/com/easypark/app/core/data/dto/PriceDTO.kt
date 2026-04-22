package com.easypark.app.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PriceDTO(
    val amount: Double? = 0.0,
    val currency: String? = "BOB"
)