package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.ReservationModel

fun ReservationModel.toEntity(driverId: Int) = ReservationEntity(
    driverId = driverId,
    spaceId = spaceId,
    startHour = startTime,
    finalHour = endTime,
    totalPrice = totalPrice.amount,
    state = status,
    methodPay = paymentMethod,
)

fun ReservationEntity.toModel(parkingName: String, address: String, spaceNumber: Int) = ReservationModel(
    id = this.id,
    spaceId = this.spaceId,
    spaceNumber = spaceNumber,
    parkingName = parkingName,
    address = address,
    startTime = this.startHour,
    endTime = this.finalHour,
    totalPrice = PriceModel(amount = this.totalPrice),
    status = this.state,
    paymentMethod = this.methodPay
)