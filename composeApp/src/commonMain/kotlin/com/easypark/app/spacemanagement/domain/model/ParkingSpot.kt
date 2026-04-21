package com.easypark.app.spacemanagement.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingSpot(
    val id: Int,
    val number: Int,
    val isOccupied: Boolean
)