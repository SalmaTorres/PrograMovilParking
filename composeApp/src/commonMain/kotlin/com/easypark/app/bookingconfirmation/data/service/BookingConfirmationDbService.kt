package com.easypark.app.bookingconfirmation.data.service

import com.easypark.app.bookingconfirmation.data.dao.BookingConfirmationDao
import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity

class BookingConfirmationDbService(
    private val dao: BookingConfirmationDao
) : BookingConfirmationLocalDataSource {
    override suspend fun save(entity: ReservationEntity): Int = dao.insert(entity).toInt()
    override suspend fun getReservationById(id: Int): ReservationEntity? = dao.getReservationById(id)
    override suspend fun getParkingById(id: Int): ParkingEntity? = dao.getParkingById(id)
    override suspend fun getFirstAvailableSpace(parkingId: Int): SpaceEntity? {
        return dao.getFirstAvailableSpace(parkingId)
    }
    override suspend fun updateSpaceStatus(spaceId: Int, newState: String) {
        dao.updateSpaceStatus(spaceId, newState)
    }
}