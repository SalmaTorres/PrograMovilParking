package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.domain.model.SpaceModel

fun SpaceModel.toEntity() = SpaceEntity(
    parkingId,
    number,
    state
)

fun SpaceEntity.toModel() =  SpaceModel(
    id,
    parkingId,
    number,
    state
)