package com.easypark.app.spacemanagement.domain.model

data class SpaceSummary(
    val totalCapacity: Int,
    val occupied: Int,
    val available: Int
)
