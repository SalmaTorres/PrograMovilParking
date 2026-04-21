package com.easypark.app.reservationhistory.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.reservationhistory.data.dao.ReservationHistoryDao
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel

class ReservationHistoryRepositoryImpl(
    private val dao: ReservationHistoryDao
) : ReservationHistoryRepository {

    override suspend fun getReservations(parkingId: Int): List<ReservationItemModel> {
        val entities = dao.getReservationsByParking(parkingId)

        return entities.map { res ->
            val user = dao.getUserById(res.driverId)
            val space = dao.getSpaceById(res.spaceId)

            ReservationItemModel(
                id = res.id,
                clientName = user?.name ?: "Cliente desconocido",
                spaceLabel = "Espacio ${space?.number ?: "?"}",
                startTime = "10:00 AM",
                endTime = "12:00 PM",
                status = if (res.state == "ACTIVE") ReservationStatus.ACTIVE else ReservationStatus.FINISHED
            )
        }
    }
}