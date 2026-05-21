package com.easypark.app.reservationsummary.data.repository

import ReservationModel
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class ReservationSummaryRepositoryImpl(
    private val localDS: ReservationSummaryLocalDataSource,
    private val firebaseManager: FirebaseManager
) : ReservationSummaryRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    override fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>> {
        return firebaseManager.observeData("reservations").map { json ->
            if (json == null) return@map emptyList<ReservationModel>()

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
                    .filter { it.driverId == userId }
                    .map { it.toDomain() }
                    .filter { it.status == "ACTIVE" }
            } catch (e: Exception) {
                io.sentry.kotlin.multiplatform.Sentry.captureException(e)
                emptyList()
            }
        }
    }

    override suspend fun getActiveReservations(userId: Int): List<ReservationModel> {
        println("DEBUG-REPO: Buscando reservas para el usuario ID: $userId")

        val entities = localDS.getReservationsByUser(userId)

        println("DEBUG-REPO: Reservas encontradas en DB: ${entities.size}")

        val activeEntities = entities.filter { it.state == "ACTIVE" }
        println("DEBUG-REPO: Reservas con estado ACTIVE: ${activeEntities.size}")

        return activeEntities.mapNotNull { res ->
            val space = localDS.getSpace(res.spaceId) ?: return@mapNotNull null
            val parking = localDS.getParking(space.parkingId) ?: return@mapNotNull null

            ReservationModel(
                id = res.id,
                parkingName = parking.name,
                address = parking.address,
                spaceId = res.spaceId,
                spaceNumber = space.number,
                startTime = res.startHour,
                endTime = res.finalHour,
                totalPrice = PriceModel(amount = res.totalPrice),
                paymentMethod = res.methodPay,
                status = res.state,
                vehiclePlate = res.vehiclePlate,
                vehicleType = res.vehicleType,
                arrivalTime = res.arrivalTime,
                parkingId = parking.id
            )
        }
    }

    override suspend fun checkInReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
        val arrivalTime = Clock.System.now().toEpochMilliseconds()
        firebaseManager.saveData("reservations/$reservationId/arrivalTime", arrivalTime.toString())
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"OCUPADO\"")
    }

    override suspend fun checkOutReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
        // 1. Mark reservation as FINISHED
        firebaseManager.saveData("reservations/$reservationId/status", "\"FINISHED\"")

        // 2. Set space state to LIBRE
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"LIBRE\"")

        // 3. Decrement occupied count in the summary
        val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
        if (summaryJson != null) {
            try {
                val element = jsonParser.parseToJsonElement(summaryJson as? String ?: "")
                if (element is kotlinx.serialization.json.JsonObject) {
                    val currentReservations = element["activeReservations"]?.toString()?.toIntOrNull() ?: 0
                    val currentOccupied = element["occupiedSpaces"]?.toString()?.toIntOrNull() ?: 0
                    
                    val newReservations = if (currentReservations > 0) currentReservations - 1 else 0
                    val newOccupied = if (currentOccupied > 0) currentOccupied - 1 else 0
                    
                    val newSummaryJson = """
                    {
                        "totalEarnings": ${element["totalEarnings"]?.toString() ?: "0.0"},
                        "activeReservations": $newReservations,
                        "occupiedSpaces": $newOccupied,
                        "totalSpaces": ${element["totalSpaces"]?.toString() ?: "0"},
                        "pricePerHour": ${element["pricePerHour"]?.toString() ?: "{}"}
                    }
                    """.trimIndent()
                    firebaseManager.saveData("parkings/$parkingId/summary", newSummaryJson)
                }
            } catch (e: Exception) {
                println("Error updating summary in check-out: ${e.message}")
            }
        }
    }

    override suspend fun checkAndEvacuateExpiredReservations() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val reservationsJson = firebaseManager.observeData("reservations").firstOrNull() ?: return
        try {
            val element = jsonParser.parseToJsonElement(reservationsJson as? String ?: "")
            val reservationsMap = if (element is kotlinx.serialization.json.JsonObject) {
                jsonParser.decodeFromJsonElement<Map<String, ReservationDTO>>(element)
            } else if (element is kotlinx.serialization.json.JsonArray) {
                jsonParser.decodeFromJsonElement<List<ReservationDTO?>>(element)
                    .filterNotNull()
                    .mapIndexed { index, dto -> dto.id.toString() to dto }
                    .toMap()
            } else {
                emptyMap()
            }

            reservationsMap.forEach { (key, dto) ->
                val endTime = dto.endTime ?: 0L
                val parkingId = dto.parkingId ?: 0
                val spaceId = dto.spaceId ?: 0
                val status = dto.status ?: "ACTIVE"

                // 5-minute warning check
                val warned = dto.warned5Min ?: false
                val driverId = dto.driverId ?: 0
                val diff = endTime - currentTime
                if (!warned && (status == "ACTIVE" || status == "RESERVADO" || status == "OCUPADO") && diff in 1..300000L && driverId > 0) {
                    val warnNotification = """
                    {
                        "id": ${dto.id ?: 0},
                        "title": "Aviso de finalización",
                        "message": "Tu tiempo de parqueo termina en 5 minutos. ¿Deseas extenderlo?",
                        "time": "Ahora",
                        "isUnread": true
                    }
                    """.trimIndent()
                    firebaseManager.saveData("notifications/$driverId/warn_${dto.id}_$currentTime", warnNotification)
                    firebaseManager.saveData("reservations/$key/warned5Min", "true")
                }

                if (endTime > 0L && currentTime > endTime && (status == "ACTIVE" || status == "OCUPADO" || status == "RESERVADO")) {
                    println("AUTO-EVACUATION: Reservation key=$key for parking=$parkingId, space=$spaceId expired!")
                    // 1. Set reservation status to "FINISHED"
                    firebaseManager.saveData("reservations/$key/status", "\"FINISHED\"")
                    
                    // 2. Set space state to "LIBRE"
                    firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"LIBRE\"")

                    // 3. Decrement occupied count in the summary
                    val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
                    if (summaryJson != null) {
                        val summaryElement = jsonParser.parseToJsonElement(summaryJson as? String ?: "")
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
            }
        } catch (e: Exception) {
            println("AUTO-EVACUATION-ERROR: ${e.message}")
        }
    }
}