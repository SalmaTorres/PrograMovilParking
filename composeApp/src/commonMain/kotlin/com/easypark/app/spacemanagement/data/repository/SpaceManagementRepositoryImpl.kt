package com.easypark.app.spacemanagement.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.dto.SpaceDTO
import com.easypark.app.core.data.mapper.toParkingSpot
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.collections.emptyList
import kotlin.collections.sortedBy

class SpaceManagementRepositoryImpl(
    private val spaceDS: SpaceLocalDataSource,
    private val firebaseManager: FirebaseManager
) : SpaceManagementRepository {

    override fun observeParkingSpots(parkingId: Int): Flow<List<ParkingSpot>> {
        return firebaseManager.observeData("spaces/$parkingId").map { json ->
            if (json == null) return@map emptyList<ParkingSpot>()

            try {
                val spacesMap = Json.decodeFromString<Map<String, SpaceDTO>>(json)
                spacesMap.values.map { it.toParkingSpot() }.sortedBy { it.number }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override fun observeSpaceSummary(parkingId: Int): Flow<SpaceSummary> {
        return observeParkingSpots(parkingId).map { spots ->
            val total = spots.size
            val occupied = spots.count { it.isOccupied }
            SpaceSummary(
                totalCapacity = total,
                occupied = occupied,
                available = total - occupied
            )
        }
    }

    override suspend fun getSpaceSummary(parkingId: Int): SpaceSummary {
        val total = spaceDS.countTotal(parkingId)
        val available = spaceDS.countAvailable(parkingId)
        val occupied = total - available

        return SpaceSummary(
            totalCapacity = total,
            occupied = occupied,
            available = available
        )
    }

    override suspend fun getParkingSpots(parkingId: Int): List<ParkingSpot> {
        val entities = spaceDS.getMySpaces(parkingId)

        return entities.map { entity ->
            ParkingSpot(
                id = entity.id,
                number = entity.number,
                isOccupied = entity.state == "OCUPADO"
            )
        }
    }
}