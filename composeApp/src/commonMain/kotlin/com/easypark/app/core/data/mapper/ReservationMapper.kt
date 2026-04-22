package com.easypark.app.core.data.mapper

import ReservationModel
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.domain.model.PriceModel

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

fun ReservationDTO.toDomain() = ReservationModel(
    id = id ?: 0,
    parkingName = parkingName ?: "",
    address = address ?: "",
    spaceId = spaceId ?: 0,
    spaceNumber = spaceNumber ?: 0,
    startTime = startTime ?: 0L,
    endTime = endTime ?: 0L,
    totalPrice = totalPrice?.toDomain() ?: PriceModel(0.0),
    paymentMethod = paymentMethod ?: "CASH",
    status = status ?: "ACTIVE"
)