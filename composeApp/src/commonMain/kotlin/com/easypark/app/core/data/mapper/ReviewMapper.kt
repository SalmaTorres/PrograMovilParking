package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ReviewEntity
import com.easypark.app.parkingdetails.data.dto.ReviewDTO
import com.easypark.app.parkingdetails.domain.model.ReviewModel

fun ReviewModel.toEntity() = ReviewEntity(
    userId,
    parkingId,
    rating
)
fun ReviewEntity.toModel() = ReviewModel(
    id,
    userId,
    parkingId,
    rating
)

fun ReviewDTO.toDomain() = ReviewModel(
    id = id ?: 0,
    userId = userId ?: 0,
    parkingId = parkingId ?: 0,
    rating = rating ?: 0f
)
