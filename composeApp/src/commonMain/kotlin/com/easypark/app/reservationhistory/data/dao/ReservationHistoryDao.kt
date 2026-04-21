package com.easypark.app.reservationhistory.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.data.entity.SpaceEntity

@Dao
interface ReservationHistoryDao {
    @Query("""
        SELECT * FROM reservation 
        WHERE spaceId IN (SELECT id FROM space WHERE parkingId = :parkingId)
    """)
    suspend fun getReservationsByParking(parkingId: Int): List<ReservationEntity>

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("SELECT * FROM space WHERE id = :spaceId")
    suspend fun getSpaceById(spaceId: Int): SpaceEntity?
}