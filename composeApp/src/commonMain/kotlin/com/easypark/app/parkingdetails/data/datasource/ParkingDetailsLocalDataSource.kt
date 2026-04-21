package com.easypark.app.parkingdetails.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReviewEntity

interface ParkingDetailsLocalDataSource {
    suspend fun getById(id: Int): ParkingEntity?
    suspend fun saveReview(entity: ReviewEntity)
    suspend fun getReviews(parkingId: Int): List<ReviewEntity>
    suspend fun countReviews(parkingId: Int): Int
    suspend fun updateAverageRating(parkingId: Int, newRating: Float)
}