package com.easypark.app.reservationsummary.data.service

import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.reservationsummary.data.dao.ReservationSummaryDao
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource

class ReservationSummaryDbService(
    private val dao: ReservationSummaryDao
) : ReservationSummaryLocalDataSource {

    override suspend fun getReservation(id: Int) = dao.getReservationById(id)

    override suspend fun getSpace(spaceId: Int) = dao.getSpaceById(spaceId)

    override suspend fun getParking(parkingId: Int) = dao.getParkingById(parkingId)

    override suspend fun getReservationsByUser(userId: Int) = dao.getReservationsByDriver(userId)
}