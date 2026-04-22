package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.status.Currency
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.registerparking.data.dto.ParkingDTO

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

fun ParkingDTO.toDomain() = ParkingModel(
    id = id ?: 0,
    name = name ?: "",
    address = address ?: "",
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    pricePerHour = pricePerHour?.toDomain() ?: PriceModel(0.0),
    rating = rating ?: 0f,
    totalSpaces = totalSpaces ?: 0,
    schedule = schedule ?: "08:00 - 22:00",
    reviewCount = reviewCount ?: 0,
    availableSpaces = availableSpaces ?: 0,
    ownerId = ownerId ?: 0
)