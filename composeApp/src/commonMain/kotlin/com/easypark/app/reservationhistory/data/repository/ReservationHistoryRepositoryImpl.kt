package com.easypark.app.reservationhistory.data.repository

import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.reservationhistory.data.dao.ReservationHistoryDao
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.*
import com.easypark.app.core.data.mapper.toDomain
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock

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
            if (json == null || json == "null") return@map emptyList<ReservationItemModel>()

            try {
                val element = jsonParser.parseToJsonElement(json as String)
                val dtoList = if (element is JsonObject) {
                    jsonParser.decodeFromJsonElement<Map<String, ReservationDTO>>(element).values.toList()
                } else if (element is JsonArray) {
                    jsonParser.decodeFromJsonElement<List<ReservationDTO?>>(element).filterNotNull()
                } else {
                    emptyList()
                }

                dtoList
                    .filter { it.parkingId == parkingId } // Filtramos por el parqueo del dueño
                    .map { dto ->
                        val model = dto.toDomain()

                        ReservationItemModel(
                            id = dto.id ?: 0,
                            clientName = dto.clientName ?: "Cliente #${dto.id}",
                            spaceLabel = "Espacio ${dto.spaceNumber ?: "?"}",
                            // Usamos el helper de tiempo relativo para realismo
                            startTime = com.easypark.app.core.util.RelativeTimeHelper.format(dto.startTime ?: 0L),
                            endTime = com.easypark.app.core.util.RelativeTimeHelper.format(dto.endTime ?: 0L),

                            // MAPEADO DE ESTADOS PARA LA UI:
                            status = when (model.status) {
                                "PENDIENTE" -> ReservationStatus.PENDIENTE
                                "OCUPADO", "ACTIVE" -> ReservationStatus.OCUPADO
                                "ENDING_SOON" -> ReservationStatus.ENDING_SOON
                                "FINISHED" -> ReservationStatus.FINISHED
                                "CANCELADO" -> ReservationStatus.CANCELADO
                                else -> ReservationStatus.PENDIENTE
                            },

                            // Datos del vehículo para que el dueño identifique al cliente
                            vehiclePlate = dto.vehiclePlate,
                            vehicleType = dto.vehicleType,

                            timeLeftText = if (model.status == "PENDIENTE") "Esperando" else "En curso"
                        )
                    }
                    .sortedByDescending { it.id } // Mostrar las más nuevas arriba
            } catch (e: Exception) {
                println("ERROR HISTORY REPO: ${e.message}")
                emptyList()
            }
        }
    }

    override suspend fun getReservations(userId: Int): List<ReservationItemModel> {
        // Fallback local en Room
        val entities = dao.getReservationsByParking(userId)
        return entities.map { res ->
            val user = dao.getUserById(res.driverId)
            val space = dao.getSpaceById(res.spaceId)

            ReservationItemModel(
                id = res.id,
                clientName = user?.name ?: "Cliente desconocido",
                spaceLabel = "Espacio ${space?.number ?: "?"}",
                startTime = "---",
                endTime = "---",
                status = if (res.state == "FINISHED") ReservationStatus.FINISHED else ReservationStatus.OCUPADO
            )
        }
    }

    override suspend fun markArrival(reservationId: Int) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()

        // 1. Obtener la reserva actual para conocer el parkingId y spaceId
        val resJson = firebaseManager.observeData("reservations/$reservationId").firstOrNull()
        if (resJson == null) return

        try {
            val resElement = jsonParser.parseToJsonElement(resJson as String).jsonObject
            val parkingId = resElement["parkingId"]?.jsonPrimitive?.intOrNull ?: return
            val spaceId = resElement["spaceId"]?.jsonPrimitive?.intOrNull ?: return

            // 2. Actualizar estado en Firebase (Reserva y Espacio Físico)
            firebaseManager.saveData("reservations/$reservationId/status", "\"OCUPADO\"")
            firebaseManager.saveData("reservations/$reservationId/arrivalTime", now.toString())
            firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"OCUPADO\"")

            // 3. ACTUALIZAR CONTADORES EN EL SUMMARY DEL PARQUEO
            val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
            if (summaryJson != null) {
                val sumElement = jsonParser.parseToJsonElement(summaryJson as String).jsonObject
                val currentOccupied = sumElement["occupiedSpaces"]?.jsonPrimitive?.intOrNull ?: 0

                // Incrementamos el contador de espacios ocupados en tiempo real
                firebaseManager.saveData("parkings/$parkingId/summary/occupiedSpaces", (currentOccupied + 1).toString())
            }

            // 4. Notificar al Conductor que su llegada fue registrada
            val driverId = resElement["driverId"]?.jsonPrimitive?.intOrNull ?: 0
            if (driverId > 0) {
                val notification = """
                {
                    "id": $reservationId,
                    "title": "¡Bienvenido!",
                    "message": "Tu llegada ha sido registrada. Tu tiempo de estancia ha comenzado.",
                    "time": "Ahora",
                    "isUnread": true
                }
                """.trimIndent()
                firebaseManager.saveData("notifications/$driverId/checkin_$now", notification)
            }

        } catch (e: Exception) {
            println("Error en markArrival: ${e.message}")
        }
    }
}