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
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

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
            if (json == null || json == "null" || (json as? String)?.isBlank() == true) return@map emptyList<ReservationModel>()

            try {
                val element = jsonParser.parseToJsonElement(json as String)
                val dtoList = if (element is kotlinx.serialization.json.JsonObject) {
                    element.values.mapNotNull { value ->
                        try {
                            jsonParser.decodeFromJsonElement<ReservationDTO>(value)
                        } catch (e: Exception) {
                            println("FAIL DECODING RESERVATION OBJECT VALUE: ${e.message} - JSON: $value")
                            null
                        }
                    }
                } else if (element is kotlinx.serialization.json.JsonArray) {
                    element.mapNotNull { value ->
                        try {
                            jsonParser.decodeFromJsonElement<ReservationDTO>(value)
                        } catch (e: Exception) {
                            println("FAIL DECODING RESERVATION ARRAY VALUE: ${e.message} - JSON: $value")
                            null
                        }
                    }
                } else {
                    emptyList()
                }

                dtoList
                    .filter { it.driverId == userId }
                    .map { it.toDomain() }
                    .filter { res ->
                        // CAMBIO AQUÍ: Aceptar todos los estados de una reserva "viva"
                        res.status == "PENDIENTE" ||
                                res.status == "OCUPADO" ||
                                res.status == "ACTIVE" ||
                                res.status == "RESERVADO"
                    }
            } catch (e: Exception) {
                println("FAIL PARSING RESERVATIONS ROOT ELEMENT: ${e.message}")
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
        val currentTime = Clock.System.now().toEpochMilliseconds()

        // 1. Obtener datos actuales de la reserva y el parking para el cálculo
        val reservationJson = firebaseManager.observeData("reservations/$reservationId").firstOrNull()
        val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()

        if (reservationJson != null && summaryJson != null) {
            try {
                val resElement = jsonParser.parseToJsonElement(reservationJson as String)
                val sumElement = jsonParser.parseToJsonElement(summaryJson as String)

                val arrivalTime = resElement.jsonObject["arrivalTime"]?.jsonPrimitive?.let { it.longOrNull ?: it.content.toLongOrNull() } ?: currentTime
                val pricePerHour = sumElement.jsonObject["pricePerHour"]?.jsonObject?.get("amount")?.jsonPrimitive?.let { it.doubleOrNull ?: it.content.toDoubleOrNull() } ?: 10.0

                // CÁLCULO REALISTA: Horas transcurridas (mínimo 1)
                val diffMillis = currentTime - arrivalTime
                val hours = maxOf(1L, diffMillis / 3600000L)
                val finalPrice = hours * pricePerHour

                // 2. Actualizar Reserva como FINISHED con el precio final calculado
                firebaseManager.saveData("reservations/$reservationId/status", "\"FINISHED\"")
                firebaseManager.saveData("reservations/$reservationId/totalPrice/amount", finalPrice.toString())
                firebaseManager.saveData("reservations/$reservationId/endTime", currentTime.toString())

                // 3. Liberar el espacio
                firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"LIBRE\"")

                // 4. Actualizar resumen de ganancias del dueño
                val currentEarnings = sumElement.jsonObject["totalEarnings"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val activeRes = sumElement.jsonObject["activeReservations"]?.jsonPrimitive?.intOrNull ?: 0
                val occupiedSp = sumElement.jsonObject["occupiedSpaces"]?.jsonPrimitive?.intOrNull ?: 0

                val newSummary = """
                {
                    "totalEarnings": ${currentEarnings + finalPrice},
                    "activeReservations": ${if (activeRes > 0) activeRes - 1 else 0},
                    "occupiedSpaces": ${if (occupiedSp > 0) occupiedSp - 1 else 0},
                    "totalSpaces": ${sumElement.jsonObject["totalSpaces"]},
                    "pricePerHour": ${sumElement.jsonObject["pricePerHour"]}
                }
                """.trimIndent()

                firebaseManager.saveData("parkings/$parkingId/summary", newSummary)

            } catch (e: Exception) {
                println("Error en el cálculo de check-out: ${e.message}")
            }
        }
    }

    suspend fun occupyParkingSpot(parkingId: Int, spaceId: Int) {
        val now = Clock.System.now().toEpochMilliseconds()
        // Marcamos el espacio como ocupado y guardamos la hora de inicio en el nodo del espacio
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"OCUPADO\"")
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/manualStartTime", now.toString())
    }

    override suspend fun checkAndEvacuateExpiredReservations() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val reservationsJson = firebaseManager.observeData("reservations").firstOrNull() ?: return
        try {
            val element = jsonParser.parseToJsonElement(reservationsJson as? String ?: "")
            val reservationsMap = if (element is kotlinx.serialization.json.JsonObject) {
                element.entries.associate { (key, value) ->
                    try {
                        key to jsonParser.decodeFromJsonElement<ReservationDTO>(value)
                    } catch (e: Exception) {
                        println("FAIL DECODING EVACUATION RESERVATION OBJECT VALUE: ${e.message} - JSON: $value")
                        key to null
                    }
                }.filterValues { it != null }.mapValues { it.value!! }
            } else if (element is kotlinx.serialization.json.JsonArray) {
                element.mapIndexedNotNull { index, value ->
                    try {
                        val dto = jsonParser.decodeFromJsonElement<ReservationDTO>(value)
                        dto.id.toString() to dto
                    } catch (e: Exception) {
                        println("FAIL DECODING EVACUATION RESERVATION ARRAY VALUE: ${e.message} - JSON: $value")
                        null
                    }
                }.toMap()
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

                // 10-minute timeout for PENDIENTE reservations
                val startTime = dto.startTime ?: 0L
                if (status == "PENDIENTE" && startTime > 0L) {
                    val elapsed = currentTime - startTime
                    if (elapsed > 600000L) { // 10 minutes
                        println("AUTO-EVACUATION: Reservation key=$key for parking=$parkingId timed out (no check-in)!")
                        firebaseManager.saveData("reservations/$key/status", "\"CANCELADO\"")
                        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"LIBRE\"")

                        // Adjust summary statistics
                        val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
                        if (summaryJson != null) {
                            val summaryElement = jsonParser.parseToJsonElement(summaryJson as? String ?: "")
                            if (summaryElement is kotlinx.serialization.json.JsonObject) {
                                val currentReservations = summaryElement["activeReservations"]?.toString()?.toIntOrNull() ?: 0
                                val currentOccupied = summaryElement["occupiedSpaces"]?.toString()?.toIntOrNull() ?: 0
                                val currentEarnings = summaryElement["totalEarnings"]?.toString()?.toDoubleOrNull() ?: 0.0
                                val priceAmount = dto.totalPrice?.amount ?: 0.0

                                val newReservations = if (currentReservations > 0) currentReservations - 1 else 0
                                val newOccupied = if (currentOccupied > 0) currentOccupied - 1 else 0
                                val newEarnings = if (currentEarnings >= priceAmount) currentEarnings - priceAmount else 0.0

                                val newSummaryJson = """
                                {
                                    "totalEarnings": $newEarnings,
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

    override suspend fun checkAndCancelReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
        firebaseManager.saveData("reservations/$reservationId/status", "\"CANCELADO\"")
        firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"LIBRE\"")
    }
}