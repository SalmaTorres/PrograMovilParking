package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.SpaceDTO
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.domain.model.SpaceModel
import com.easypark.app.spacemanagement.domain.model.ParkingSpot

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

fun SpaceDTO.toDomain() = SpaceModel(
    id = id ?: 0,
    parkingId = parkingId ?: 0,
    number = number ?: 0,
    state = state ?: "LIBRE"
)

fun SpaceDTO.toParkingSpot() = ParkingSpot(
    id = this.id ?: 0,
    number = this.number ?: 0,
    isOccupied = this.state == "OCUPADO"
)