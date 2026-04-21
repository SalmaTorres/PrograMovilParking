package com.easypark.app.reservationsummary.data.repository

import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.ReservationModel
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository

class ReservationSummaryRepositoryImpl(
    private val localDS: ReservationSummaryLocalDataSource
) : ReservationSummaryRepository {

    override suspend fun getReservationSummary(id: Int): ReservationModel {

        val res = localDS.getReservation(id) ?: throw Exception("Reserva no encontrada")

        val space = localDS.getSpace(res.spaceId) ?: throw Exception("Espacio no encontrado")

        val parking = localDS.getParking(space.parkingId) ?: throw Exception("Parqueo no encontrado")

        return ReservationModel(
            id = res.id,
            parkingName = parking.name,
            address = parking.address,
            spaceNumber = space.number,
            startTime = res.startHour,
            endTime = res.finalHour,
            totalPrice = PriceModel(amount = res.totalPrice),
            paymentMethod = res.methodPay
        )
    }
}