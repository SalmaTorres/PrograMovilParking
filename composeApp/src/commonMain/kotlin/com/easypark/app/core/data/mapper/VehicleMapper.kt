package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.VehicleEntity
import com.easypark.app.registervehicle.data.dto.VehicleDTO
import com.easypark.app.registervehicle.domain.model.VehicleModel

fun VehicleModel.toEntity(driverId: Int) = VehicleEntity(
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

fun VehicleDTO.toDomain() = VehicleModel(
    id = id ?: 0,
    driverId = driverId ?: 0,
    plate = plate ?: "",
    model = model ?: "",
    color = color ?: ""
)