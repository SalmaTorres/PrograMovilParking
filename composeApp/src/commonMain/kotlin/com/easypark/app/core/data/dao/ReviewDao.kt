package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.easypark.app.core.data.entity.ReviewEntity

@Dao
interface ReviewDao {
    @Insert
    suspend fun insert(review: ReviewEntity)

    @Query("SELECT * FROM review WHERE parkingId = :parkingId")
    suspend fun getByParking(parkingId: Int): List<ReviewEntity>

    @Query("SELECT COUNT(*) FROM review WHERE parkingId = :parkingId")
    suspend fun countByParking(parkingId: Int): Int
}