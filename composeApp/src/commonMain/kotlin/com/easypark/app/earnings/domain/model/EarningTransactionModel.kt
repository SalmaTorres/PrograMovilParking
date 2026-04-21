package com.easypark.app.earnings.domain.model

import kotlinx.serialization.Serializable

data class EarningTransactionModel(
    val id: Int,
    val date: String,
    val label: String,
    val amount: Double,
    val currency: String = "BS"
)