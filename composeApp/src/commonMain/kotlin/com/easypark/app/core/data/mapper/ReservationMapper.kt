package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.domain.model.Currency
import com.easypark.app.core.domain.model.Price
import com.easypark.app.core.domain.model.ReservationModel

fun ReservationModel.toEntity() = ReservationEntity(
    spaceId,
    driverId,
    startHour,
    finalHour,
    totalPrice = totalPrice.amount,
    state,
    methodPay
)

fun ReservationEntity.toModel() =  ReservationModel(
    id,
    spaceId,
    driverId,
    startHour,
    finalHour,
    totalPrice = Price(totalPrice, Currency.BOB),
    state,
    methodPay
)