package com.easypark.app.core.domain.model

data class IncomeTransactionModel(
    val id: Int,
    val date: String,          // "1 NOV, 12:30 PM"
    val title: String,         // "Reserva A-05"
    val amount: Double,        // 15.00
    val currency: String       // "BS" o "$"
)