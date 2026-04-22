package com.easypark.app.parkingdetails.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDTO (
    val id: Int? = 0,
    val userId: Int? = 0,
    val parkingId: Int? = 0,
    val rating: Float? = 0f
)