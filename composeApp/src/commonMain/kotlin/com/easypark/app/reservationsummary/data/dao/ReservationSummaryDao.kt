package com.easypark.app.reservationsummary.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity

@Dao
interface ReservationSummaryDao {
    @Query("SELECT * FROM reservation WHERE id = :id")
    suspend fun getReservationById(id: Int): ReservationEntity?

    @Query("SELECT * FROM space WHERE id = :spaceId")
    suspend fun getSpaceById(spaceId: Int): SpaceEntity?

    @Query("SELECT * FROM parking WHERE id = :parkingId")
    suspend fun getParkingById(parkingId: Int): ParkingEntity?

    @Query("SELECT * FROM reservation WHERE driverId = :userId") // <-- Revisa si es driverId
    suspend fun getReservationsByDriver(userId: Int): List<ReservationEntity>
}