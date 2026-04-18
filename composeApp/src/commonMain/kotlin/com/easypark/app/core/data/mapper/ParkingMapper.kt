package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.Price
import com.easypark.app.core.domain.model.Currency

fun ParkingModel.toEntity() = ParkingEntity(
    ownerId,
    name,
    address,
    latitude,
    longitude,
    pricePerHour = pricePerHour.amount,
    rating,
    totalSpaces
)

fun ParkingEntity.toModel() =  ParkingModel(
    id,
    ownerId,
    name,
    address,
    latitude,
    longitude,
    pricePerHour = Price(pricePerHour, Currency.BOB),
    rating,
    totalSpaces
)