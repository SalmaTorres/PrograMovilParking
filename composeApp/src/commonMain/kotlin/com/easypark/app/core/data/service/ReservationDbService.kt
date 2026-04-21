package com.easypark.app.core.data.service

import com.easypark.app.core.data.dao.ReservationDao
import com.easypark.app.core.data.datasource.ReservationLocalDataSource
import com.easypark.app.core.data.entity.ReservationEntity

class ReservationDbService(
    private val reservationDao: ReservationDao
) : ReservationLocalDataSource {

    override suspend fun readByParking(parkingId: Int): List<ReservationEntity> {
        return reservationDao.getReservationsByParking(parkingId)
    }
}