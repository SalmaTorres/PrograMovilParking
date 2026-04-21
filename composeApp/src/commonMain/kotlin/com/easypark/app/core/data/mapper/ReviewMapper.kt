package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ReviewEntity
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