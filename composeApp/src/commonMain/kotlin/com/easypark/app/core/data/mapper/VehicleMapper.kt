package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.VehicleEntity
import com.easypark.app.registervehicle.data.dto.VehicleDTO
import com.easypark.app.registervehicle.domain.model.VehicleModel

fun VehicleModel.toEntity(driverId: Int) = VehicleEntity(
    driverId = driverId,
    plate = plate,
    type = type,
    model = model,
    color = color
)

fun VehicleEntity.toModel() = VehicleModel(
    id = id,
    driverId = driverId,
    plate = plate,
    type = type,
    model = model,
    color = color
)

fun VehicleDTO.toDomain() = VehicleModel(
    id = id ?: 0,
    driverId = driverId ?: 0,
    plate = plate ?: "",
    type = type ?: "Automóvil",
    model = model ?: "",
    color = color ?: ""
)

fun VehicleModel.toRemote(driverId: Int) = VehicleDTO(
    id = id,
    driverId = driverId,
    plate = plate,
    type = type,
    model = model,
    color = color
)
