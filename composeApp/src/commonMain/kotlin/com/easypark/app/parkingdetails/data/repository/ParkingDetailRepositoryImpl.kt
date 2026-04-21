package com.easypark.app.parkingdetails.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.entity.ReviewEntity
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailsRepository

class ParkingDetailRepositoryImpl(
    private val localDS: ParkingDetailsLocalDataSource,
    private val spaceDS: SpaceLocalDataSource
) : ParkingDetailsRepository {

    override suspend fun getParkingDetail(id: Int): ParkingModel {
        val entity = localDS.getById(id) ?: throw Exception("No encontrado")

        val total = spaceDS.countTotal(id)
        val available = spaceDS.countAvailable(id)

        val reviewCount = localDS.countReviews(id)

        return entity.toModel(availableSpaces = available, reviewCount = reviewCount)
            .copy(totalSpaces = total)
    }

    override suspend fun rateParking(parkingId: Int, userId: Int, rating: Float) {
        localDS.saveReview(ReviewEntity(userId = userId, parkingId = parkingId, rating = rating))

        val allReviews = localDS.getReviews(parkingId)
        val newAverage = if (allReviews.isEmpty()) 0f else allReviews.map { it.rating }.average().toFloat()

        localDS.updateAverageRating(parkingId, newAverage)
    }
}