package com.easypark.app.reservationhistory.data.repository

import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.reservationhistory.data.dao.ReservationHistoryDao
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import com.easypark.app.core.data.mapper.toDomain

class ReservationHistoryRepositoryImpl(
    private val dao: ReservationHistoryDao,
    private val firebaseManager: FirebaseManager
) : ReservationHistoryRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    override fun observeReservationsRealtime(parkingId: Int): Flow<List<ReservationItemModel>> {
         return firebaseManager.observeData("reservations").map { json ->
            if (json == null) return@map emptyList<ReservationItemModel>()

            try {
                val element = jsonParser.parseToJsonElement(json)
                val dtoList = if (element is kotlinx.serialization.json.JsonObject) {
                    jsonParser.decodeFromJsonElement<Map<String, ReservationDTO>>(element).values.toList()
                } else if (element is kotlinx.serialization.json.JsonArray) {
                    jsonParser.decodeFromJsonElement<List<ReservationDTO?>>(element).filterNotNull()
                } else {
                    emptyList()
                }

                dtoList
                    .filter { it.id != null && it.parkingId == parkingId }
                    .map { dto ->
                        val model = dto.toDomain()
                        ReservationItemModel(
                            id = dto.id ?: 0,
                            clientName = dto.clientName ?: "Usuario ${dto.id}",
                            spaceLabel = "Espacio ${dto.spaceNumber ?: "?"}",
                            startTime = "10:00 AM",
                            endTime = "12:00 PM",
                            status = if (model.status == "ACTIVE") ReservationStatus.ACTIVE else ReservationStatus.FINISHED
                        )
                    }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getReservations(parkingId: Int): List<ReservationItemModel> {
        val entities = dao.getReservationsByParking(parkingId)

        return entities.map { res ->
            val user = dao.getUserById(res.driverId)
            val space = dao.getSpaceById(res.spaceId)

            ReservationItemModel(
                id = res.id,
                clientName = user?.name ?: "Cliente desconocido",
                spaceLabel = "Espacio ${space?.number ?: "?"}",
                startTime = "10:00 AM",
                endTime = "12:00 PM",
                status = if (res.state == "ACTIVE") ReservationStatus.ACTIVE else ReservationStatus.FINISHED
            )
        }
    }
}