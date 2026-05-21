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
                            startTime = com.easypark.app.core.util.RelativeTimeHelper.format(dto.startTime ?: 0L),
                            endTime = com.easypark.app.core.util.RelativeTimeHelper.format(dto.endTime ?: 0L),
                            status = if (model.status == "PENDIENTE") ReservationStatus.ACTIVE else if (model.status == "OCUPADO") ReservationStatus.ENDING_SOON else ReservationStatus.FINISHED,
                            timeLeftText = if (model.status == "OCUPADO") "Ocupado" else "Pendiente"
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

    override suspend fun markArrival(reservationId: Int) {
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        firebaseManager.saveData("reservations/$reservationId/status", "\"OCUPADO\"")
        firebaseManager.saveData("reservations/$reservationId/arrivalTime", now.toString())
        
        // Send check-in notification to driver
        try {
            val jsonStr = firebaseManager.observeData("reservations/$reservationId").kotlinx.coroutines.flow.firstOrNull()
            if (jsonStr != null) {
                val element = jsonParser.parseToJsonElement(jsonStr)
                if (element is kotlinx.serialization.json.JsonObject) {
                    val driverId = element["driverId"]?.toString()?.toIntOrNull() ?: 0
                    if (driverId > 0) {
                        val checkInNotification = """
                        {
                            "id": $reservationId,
                            "title": "Llegada Registrada",
                            "message": "Tu vehículo ha sido marcado como llegado exitosamente. ¡Gracias por usar EasyPark!",
                            "time": "Ahora",
                            "isUnread": true
                        }
                        """.trimIndent()
                        firebaseManager.saveData("notifications/$driverId/checkin_${reservationId}_$now", checkInNotification)
                    }
                }
            }
        } catch (e: Exception) {
            println("Error sending checkin notification: ${e.message}")
        }
    }
}