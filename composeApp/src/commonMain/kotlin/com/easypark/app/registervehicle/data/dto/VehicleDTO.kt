package com.easypark.app.registervehicle.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDTO (
    val id: Int? = 0,
    val driverId: Int? = 0,
    val plate: String? = "",
    val model: String? = "",
    val color: String? = ""
)