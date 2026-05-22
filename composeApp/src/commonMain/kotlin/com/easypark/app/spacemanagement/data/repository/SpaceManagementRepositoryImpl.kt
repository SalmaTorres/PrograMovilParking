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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.collections.emptyList
import kotlin.collections.sortedBy
import kotlin.time.Clock
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull

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
                isOccupied = entity.state == "OCUPADO" || entity.state == "RESERVADO",
                state = entity.state
            )
        }
    }

    override suspend fun releaseParkingSpot(parkingId: Int, spaceId: Int) {
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"LIBRE\"")

        val reservationsJson = firebaseManager.observeData("reservations").firstOrNull()
        if (reservationsJson != null) {
            try {
                val element = jsonConfig.parseToJsonElement(reservationsJson as? String ?: "")
                val reservationsMap = if (element is kotlinx.serialization.json.JsonObject) {
                    jsonConfig.decodeFromJsonElement<Map<String, com.easypark.app.core.data.dto.ReservationDTO>>(element)
                } else if (element is kotlinx.serialization.json.JsonArray) {
                    jsonConfig.decodeFromJsonElement<List<com.easypark.app.core.data.dto.ReservationDTO?>>(element)
                        .filterNotNull()
                        .mapIndexed { index, dto -> dto.id.toString() to dto }
                        .toMap()
                } else {
                    emptyMap()
                }

                val activeRes = reservationsMap.entries.firstOrNull { (_, dto) ->
                    dto.parkingId == parkingId && dto.spaceId == spaceId && (dto.status == "ACTIVE" || dto.status == "OCUPADO" || dto.status == "RESERVADO")
                }

                if (activeRes != null) {
                    val resKey = activeRes.key
                    firebaseManager.saveData("reservations/$resKey/status", "\"FINISHED\"")
                    
                    val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
                    if (summaryJson != null) {
                        val summaryElement = jsonConfig.parseToJsonElement(summaryJson as? String ?: "")
                        if (summaryElement is kotlinx.serialization.json.JsonObject) {
                            val currentReservations = summaryElement["activeReservations"]?.toString()?.toIntOrNull() ?: 0
                            val currentOccupied = summaryElement["occupiedSpaces"]?.toString()?.toIntOrNull() ?: 0
                            
                            val newReservations = if (currentReservations > 0) currentReservations - 1 else 0
                            val newOccupied = if (currentOccupied > 0) currentOccupied - 1 else 0
                            
                            val newSummaryJson = """
                            {
                                "totalEarnings": ${summaryElement["totalEarnings"]?.toString() ?: "0.0"},
                                "activeReservations": $newReservations,
                                "occupiedSpaces": $newOccupied,
                                "totalSpaces": ${summaryElement["totalSpaces"]?.toString() ?: "0"},
                                "pricePerHour": ${summaryElement["pricePerHour"]?.toString() ?: "{}"}
                            }
                            """.trimIndent()
                            firebaseManager.saveData("parkings/$parkingId/summary", newSummaryJson)
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error manual releasing reservation: ${e.message}")
            }
        }
    }

    override suspend fun occupyParkingSpot(parkingId: Int, spaceId: Int) {
        val now = Clock.System.now().toEpochMilliseconds()
        // 1. Ocupar el espacio
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"OCUPADO\"")

        // 2. ACTUALIZAR EL SUMMARY (Para que Earnings cambie)
        val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
        if (summaryJson != null) {
            // 1. Convertimos a JsonObject primero
            val element = jsonConfig.parseToJsonElement(summaryJson as String).jsonObject

            // 2. Ahora sí podemos usar jsonPrimitive e intOrNull
            val currentOccupied = element["occupiedSpaces"]?.jsonPrimitive?.intOrNull ?: 0

            // 3. Guardamos el nuevo valor (+1)
            firebaseManager.saveData("parkings/$parkingId/summary/occupiedSpaces", (currentOccupied + 1).toString())
        }
    }
}