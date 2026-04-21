package com.easypark.app.parkingdetails.data.dao

import androidx.room.*
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReviewEntity

@Dao
interface ParkingDetailDao {
    @Query("SELECT * FROM parking WHERE id = :id")
    suspend fun getById(id: Int): ParkingEntity?

    @Query("UPDATE parking SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Float)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT * FROM review WHERE parkingId = :parkingId")
    suspend fun getReviewsByParking(parkingId: Int): List<ReviewEntity>

    @Query("SELECT COUNT(*) FROM review WHERE parkingId = :parkingId")
    suspend fun countReviewsByParking(parkingId: Int): Int
}