package com.easypark.app.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpaceDTO (
    val id: Int? = 0,
    val parkingId: Int? = 0,
    val number: Int? = 0,
    val state: String? = "LIBRE"
)