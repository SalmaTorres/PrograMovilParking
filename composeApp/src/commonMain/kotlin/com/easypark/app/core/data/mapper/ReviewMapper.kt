package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ReviewEntity
import com.easypark.app.core.domain.model.ReviewModel

fun ReviewEntity.toModel() = ReviewModel(
    id = id,
    idUser = "Usuario",
    rating = rating
)