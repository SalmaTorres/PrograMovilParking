package com.easypark.app.spacemanagement.domain.model

data class ParkingSpot(
    val id: String,
    val code: String, // e.g. "A-01"
    val isOccupied: Boolean // true for "Ocupado", false for "Libre"
)
