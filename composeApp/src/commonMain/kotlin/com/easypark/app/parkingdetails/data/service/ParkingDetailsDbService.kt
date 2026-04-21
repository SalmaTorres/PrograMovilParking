package com.easypark.app.parkingdetails.data.service

import com.easypark.app.parkingdetails.data.dao.ParkingDetailDao
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReviewEntity
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource

class ParkingDetailsDbService(
    private val parkingDao: ParkingDetailDao,
) : ParkingDetailsLocalDataSource {

    override suspend fun getById(id: Int): ParkingEntity? = parkingDao.getById(id)

    override suspend fun saveReview(entity: ReviewEntity) {
        parkingDao.insertReview(entity)
    }

    override suspend fun getReviews(parkingId: Int): List<ReviewEntity> {
        return parkingDao.getReviewsByParking(parkingId)
    }

    override suspend fun countReviews(parkingId: Int): Int {
        return parkingDao.countReviewsByParking(parkingId)
    }

    override suspend fun updateAverageRating(parkingId: Int, newRating: Float) {
        parkingDao.updateRating(parkingId, newRating)
    }
}