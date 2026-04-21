package com.easypark.app.reservationsummary.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.domain.model.ReservationModel

interface ReservationSummaryLocalDataSource {
    suspend fun getReservation(id: Int): ReservationEntity?
    suspend fun getSpace(spaceId: Int): SpaceEntity?
    suspend fun getParking(parkingId: Int): ParkingEntity?
    suspend fun getReservationsByUser(userId: Int): List<ReservationEntity>
}