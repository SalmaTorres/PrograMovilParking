package com.easypark.app.bookingconfirmation.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity

interface BookingConfirmationLocalDataSource {
    suspend fun save(entity: ReservationEntity): Int
    suspend fun getReservationById(id: Int): ReservationEntity?
    suspend fun getParkingById(id: Int): ParkingEntity?
    suspend fun getFirstAvailableSpace(parkingId: Int): SpaceEntity?
    suspend fun updateSpaceStatus(spaceId: Int, newState: String)
}