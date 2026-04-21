package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.status.Currency
import com.easypark.app.core.domain.model.PriceModel

fun ParkingModel.toEntity(ownerId: Int) = ParkingEntity(
    ownerId = ownerId,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    pricePerHour = pricePerHour.amount,
    rating = rating,
    totalSpaces = totalSpaces,
    schedule = schedule
)

fun ParkingEntity.toModel(availableSpaces: Int = 0, reviewCount: Int = 0) = ParkingModel(
    id = id,
    ownerId = ownerId,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    pricePerHour = PriceModel(pricePerHour, Currency.BOB),
    rating = rating,
    totalSpaces = totalSpaces,
    schedule = schedule,
    reviewCount = reviewCount,
    availableSpaces = availableSpaces,
    isAvailable = availableSpaces > 0
)