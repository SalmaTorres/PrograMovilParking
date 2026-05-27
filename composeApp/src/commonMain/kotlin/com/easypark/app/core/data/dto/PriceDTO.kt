package com.easypark.app.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PriceDTO(
    @Serializable(with = SafeDoubleSerializer::class) val amount: Double? = 0.0,
    val currency: String? = "BOB"
)