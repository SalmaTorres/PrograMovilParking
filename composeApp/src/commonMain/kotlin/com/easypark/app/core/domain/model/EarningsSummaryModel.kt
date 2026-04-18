package com.easypark.app.core.domain.model

data class EarningsSummaryModel(
    val totalEarnings: Double,           // $4250.00
    val percentageChange: Double,        // +12.5
    val activeReservationsCount: Int,    // 18
    val occupiedSpaces: Int,             // 42
    val totalSpaces: Int,                // 50
    val isCapacityLimited: Boolean       // Para mostrar el texto "Capacidad limitada" en rojo
)