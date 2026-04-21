package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.SpaceEntity

@Dao
interface SpaceDao {
    @Query("SELECT * FROM space WHERE id = :id")
    suspend fun getById(id: Int): SpaceEntity?

    @Query("SELECT * FROM space WHERE parkingId = :parkingId")
    suspend fun getMySpaces(parkingId: Int): List<SpaceEntity>
    @Query("SELECT COUNT(*) FROM space WHERE parkingId = :parkingId")
    suspend fun countTotal(parkingId: Int): Int

    @Query("SELECT COUNT(*) FROM space WHERE parkingId = :parkingId AND state = 'LIBRE'")
    suspend fun countAvailable(parkingId: Int): Int
}