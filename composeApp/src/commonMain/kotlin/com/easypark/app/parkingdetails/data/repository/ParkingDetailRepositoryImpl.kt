package com.easypark.app.parkingdetails.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.entity.ReviewEntity
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailsRepository
import com.easypark.app.registerparking.data.dto.ParkingDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ParkingDetailRepositoryImpl(
    private val localDS: ParkingDetailsLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val firebaseManager: FirebaseManager
) : ParkingDetailsRepository {

    override fun observeParkingDetail(id: Int): Flow<ParkingModel?> {
        return firebaseManager.observeData("parkings/$id").map { json ->
            if (json == null) return@map null

            val dto = Json.decodeFromString<ParkingDTO>(json)
            dto.toDomain()
        }
    }

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

        firebaseManager.saveData("parkings/$parkingId/rating", newAverage.toString())
        firebaseManager.saveData("parkings/$parkingId/reviewCount", allReviews.size.toString())
    }
}