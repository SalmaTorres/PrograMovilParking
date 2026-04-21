package com.easypark.app.spacemanagement.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpaceSummary(
    val totalCapacity: Int,
    val occupied: Int,
    val available: Int
)