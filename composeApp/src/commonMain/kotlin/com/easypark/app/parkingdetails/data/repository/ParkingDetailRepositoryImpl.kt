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
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class ParkingDetailRepositoryImpl(
    private val localDS: ParkingDetailsLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val observeRemoteData: (String) -> Flow<String?>,
    private val saveRemoteData: suspend (String, String) -> Unit
) : ParkingDetailsRepository {

    constructor(
        localDS: ParkingDetailsLocalDataSource,
        spaceDS: SpaceLocalDataSource,
        firebaseManager: FirebaseManager
    ) : this(localDS, spaceDS, firebaseManager::observeData, firebaseManager::saveData)

    override fun observeParkingDetail(id: Int): Flow<ParkingModel?> {
        return observeRemoteData("parkings/$id").map { json ->
            if (json == null) return@map null

            val jsonConfig = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
            val dto = jsonConfig.decodeFromString<ParkingDTO>(json)
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

        saveRemoteData("parkings/$parkingId/rating", newAverage.toString())
        saveRemoteData("parkings/$parkingId/reviewCount", allReviews.size.toString())

        val reviewId = Clock.System.now().toEpochMilliseconds()
        val reviewJson = """
        {
            "id": $reviewId,
            "userId": $userId,
            "rating": $rating,
            "timestamp": $reviewId
        }
        """.trimIndent()
        saveRemoteData("reviews/$parkingId/$reviewId", reviewJson)
    }
}
