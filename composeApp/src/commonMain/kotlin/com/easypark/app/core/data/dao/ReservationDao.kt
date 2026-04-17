package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.ReservationEntity

@Dao
interface ReservationDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reservation: ReservationEntity)

    @Query("SELECT * FROM reservation WHERE id = :id")
    suspend fun getById(id: Int): ReservationEntity?

    @Query("SELECT * FROM reservation WHERE driverId = :driverId")
    suspend fun getMyReservations(driverId: Int): List<ReservationEntity>

    @Query("UPDATE reservation SET state = :newState WHERE id = :id")
    suspend fun updateStatus(id: Int, newState: String)

    @Query("DELETE FROM reservation")
    suspend fun deleteAll()
}