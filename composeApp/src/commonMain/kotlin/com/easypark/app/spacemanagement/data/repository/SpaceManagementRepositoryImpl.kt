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
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.collections.emptyList
import kotlin.collections.sortedBy

class SpaceManagementRepositoryImpl(
    private val spaceDS: SpaceLocalDataSource,
    private val firebaseManager: FirebaseManager
) : SpaceManagementRepository {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true // Si Firebase tiene campos extra, no crashea
        isLenient = true         // Permite formatos de texto más flexibles
        coerceInputValues = true // Si llega un null donde no debe, usa el valor por defecto
    }

    override fun observeParkingSpots(parkingId: Int): Flow<List<ParkingSpot>> {
        // RUTA CORRECTA: "spaces/$parkingId"
        return firebaseManager.observeData("spaces/$parkingId").map { json ->
            if (json == null || json == "null" || json == "{}") return@map emptyList()
            try {
                val element = jsonConfig.parseToJsonElement(json)
                if (element is kotlinx.serialization.json.JsonObject) {
                    val spacesMap = jsonConfig.decodeFromJsonElement<Map<String, SpaceDTO>>(element)
                    spacesMap.values.map { it.toParkingSpot() }.sortedBy { it.number }
                } else if (element is kotlinx.serialization.json.JsonArray) {
                    val spacesList = jsonConfig.decodeFromJsonElement<List<SpaceDTO?>>(element)
                    spacesList.filterNotNull().map { it.toParkingSpot() }.sortedBy { it.number }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                println("ERROR_SPACES: ${e.message}")
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