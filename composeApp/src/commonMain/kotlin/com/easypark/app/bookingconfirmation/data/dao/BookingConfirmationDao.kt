package com.easypark.app.bookingconfirmation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity

@Dao
interface BookingConfirmationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: ReservationEntity): Long

    @Query("SELECT * FROM space WHERE parkingId = :parkingId AND state = 'LIBRE' ORDER BY number ASC LIMIT 1")
    suspend fun getFirstAvailableSpace(parkingId: Int): SpaceEntity?

    @Query("SELECT * FROM reservation WHERE id = :id")
    suspend fun getReservationById(id: Int): ReservationEntity?
    @Query("SELECT * FROM parking WHERE id = :id LIMIT 1")
    suspend fun getParkingById(id: Int): ParkingEntity?

    @Query("UPDATE space SET state = :newState WHERE id = :id")
    suspend fun updateSpaceStatus(id: Int, newState: String)

    @Query("SELECT * FROM reservation WHERE isSynced = 0")
    suspend fun getUnsyncedReservations(): List<ReservationEntity>

    @Query("UPDATE reservation SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}