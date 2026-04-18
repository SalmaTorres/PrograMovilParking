package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.VehicleEntity
import com.easypark.app.core.domain.model.VehicleModel

fun VehicleModel.toEntity() = VehicleEntity(
    driverId,
    plate,
    model,
    color
)

fun VehicleEntity.toModel() =  VehicleModel(
    id,
    driverId,
    plate,
    model,
    color
)