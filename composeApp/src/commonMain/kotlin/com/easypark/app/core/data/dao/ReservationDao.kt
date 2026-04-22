package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.ReservationEntity

@Dao
interface ReservationDao {
    @Query("""
        SELECT r.* FROM reservation r 
        INNER JOIN space s ON r.spaceId = s.id 
        WHERE s.parkingId = :parkingId
    """)
    suspend fun getReservationsByParking(parkingId: Int): List<ReservationEntity>

    @Query("SELECT COUNT(*) FROM reservation")
    suspend fun getReservationCount(): Int

    @Query("DELETE FROM reservation WHERE id IN (SELECT id FROM reservation ORDER BY id ASC LIMIT :count)")
    suspend fun deleteOldestReservations(count: Int)

    @Query("SELECT id FROM reservation ORDER BY id ASC LIMIT :count")
    suspend fun getOldestReservationIds(count: Int): List<Int>
}