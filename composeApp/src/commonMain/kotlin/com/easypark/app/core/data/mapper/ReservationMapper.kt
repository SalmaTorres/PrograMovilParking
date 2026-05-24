package com.easypark.app.core.data.mapper

import ReservationModel
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.domain.model.PriceModel
import kotlinx.datetime.Clock

fun ReservationModel.toEntity(driverId: Int) = ReservationEntity(
    driverId = driverId,
    spaceId = spaceId,
    startHour = startTime,
    finalHour = endTime,
    totalPrice = totalPrice.amount,
    state = status,
    methodPay = paymentMethod,
    vehiclePlate = vehiclePlate,
    vehicleType = vehicleType,
    arrivalTime = arrivalTime
)

fun ReservationEntity.toModel(parkingName: String, address: String, spaceNumber: Int, parkingId: Int) = ReservationModel(
    id = this.id,
    spaceId = this.spaceId,
    spaceNumber = spaceNumber,
    parkingName = parkingName,
    address = address,
    startTime = this.startHour,
    endTime = this.finalHour,
    totalPrice = PriceModel(amount = this.totalPrice),
    status = this.state,
    paymentMethod = this.methodPay,
    vehiclePlate = this.vehiclePlate,
    vehicleType = this.vehicleType,
    arrivalTime = this.arrivalTime,
    parkingId = parkingId
)

fun ReservationDTO.toDomain(): ReservationModel {
    // Usar la zona horaria del sistema para comparar correctamente
    val now = Clock.System.now()
    val currentTime = now.toEpochMilliseconds()
    val finalEndTime = endTime ?: 0L

    val rawStatus = status ?: "PENDIENTE"

    // Solo finalizar automáticamente si NO es PENDIENTE.
    // Una reserva pendiente no puede "terminar" si ni siquiera ha empezado (Check-in).
    val finalStatus = if (rawStatus != "PENDIENTE" && finalEndTime > 0L && currentTime > finalEndTime) {
        "FINISHED"
    } else {
        rawStatus
    }

    return ReservationModel(
        id = id ?: 0,
        parkingName = parkingName ?: "",
        address = address ?: "",
        spaceId = spaceId ?: 0,
        spaceNumber = spaceNumber ?: 0,
        startTime = startTime ?: 0L,
        endTime = finalEndTime,
        totalPrice = totalPrice?.toDomain() ?: PriceModel(0.0),
        paymentMethod = paymentMethod ?: "CASH",
        status = finalStatus,
        vehiclePlate = vehiclePlate ?: "",
        vehicleType = vehicleType ?: "",
        arrivalTime = arrivalTime ?: 0L,
        parkingId = parkingId ?: 0
    )
}