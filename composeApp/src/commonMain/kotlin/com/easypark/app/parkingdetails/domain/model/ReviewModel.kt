package com.easypark.app.parkingdetails.domain.model

data class ReviewModel(
    val id: Int,
    val userId: Int,
    val parkingId: Int,
    val rating: Float
)