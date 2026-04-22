package com.easypark.app.bookingconfirmation.data.repository

import ReservationModel
import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.decodeFromJsonElement

class BookingConfirmationRepositoryImpl(
    private val bookingDS: BookingConfirmationLocalDataSource,
    private val notificationDS: NotificationLocalDataSource,
    private val firebaseManager: FirebaseManager
) : BookingConfirmationRepository {
    override suspend fun observeBookingRealtime(bookingId: String): Flow<ReservationModel?> {
        return firebaseManager.observeData("reservations/$bookingId").map { json ->
            if (json == null) return@map null

            val jsonConfig = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
            val dto = jsonConfig.decodeFromString<ReservationDTO>(json)
            dto.toDomain()
        }
    }

    override suspend fun getBookingInfo(parkingId: Int): BookingConfirmationModel {
        val parking = bookingDS.getParkingById(parkingId) ?: throw Exception("No encontrado")
        return BookingConfirmationModel(
            parkingId = parking.id,
            locationName = parking.name,
            address = parking.address,
            pricePerHour = parking.pricePerHour,
            totalCost = parking.pricePerHour,
            spaceIdentifier = "Asignación automática"
        )
    }

    override suspend fun makeReservation(parkingId: Int, driverId: Int, duration: Int, paymentMethod: String, clientName: String): Int? {
        val spacesJson = firebaseManager.observeData("spaces/$parkingId").firstOrNull() ?: return null
        val jsonConfig = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true; encodeDefaults = true }
        
        val spacesMap = try {
            val element = jsonConfig.parseToJsonElement(spacesJson)
            if (element is kotlinx.serialization.json.JsonObject) {
                jsonConfig.decodeFromJsonElement<Map<String, com.easypark.app.core.data.dto.SpaceDTO>>(element)
            } else if (element is kotlinx.serialization.json.JsonArray) {
                val list = jsonConfig.decodeFromJsonElement<List<com.easypark.app.core.data.dto.SpaceDTO?>>(element)
                list.mapIndexedNotNull { index, dto -> if (dto != null) "s$index" to dto else null }.toMap()
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            return null
        }
        val spaceDTO = spacesMap.values.firstOrNull { it.state == "LIBRE" } ?: return null
        val spaceId = spaceDTO.id ?: return null
        
        val parking = bookingDS.getParkingById(parkingId) ?: return null

        val startTime = Clock.System.now().toEpochMilliseconds()
        val durationMillis = duration * 3600000L
        val reservationPrice = parking.pricePerHour * duration
        val entity = ReservationEntity(
            spaceId = spaceId,
            driverId = driverId,
            startHour = startTime,
            finalHour = startTime + durationMillis,
            totalPrice = reservationPrice,
            state = "ACTIVE",
            methodPay = paymentMethod
        )

        val resId = bookingDS.save(entity)

        if (resId != null) {
            val firebaseData = """
            {
                "id": $resId,
                "status": "ACTIVE",
                "parkingId": $parkingId,
                "parkingName": "${parking.name}",
                "address": "${parking.address}",
                "spaceId": $spaceId,
                "spaceNumber": ${spaceDTO.number},
                "driverId": $driverId,
                "clientName": "$clientName",
                "paymentMethod": "$paymentMethod",
                "totalPrice": { "amount": $reservationPrice, "currency": "BOB" }
            }
            """.trimIndent()

            firebaseManager.saveData("reservations/$resId", firebaseData)
            firebaseManager.saveData("spaces/$parkingId/s$spaceId/state", "\"OCUPADO\"")

            // Actualizar Summary
            val summaryJson = firebaseManager.observeData("parkings/$parkingId/summary").firstOrNull()
            if (summaryJson != null) {
                try {
                    val element = jsonConfig.parseToJsonElement(summaryJson)
                    if (element is kotlinx.serialization.json.JsonObject) {
                        val currentEarnings = element["totalEarnings"]?.toString()?.toDoubleOrNull() ?: 0.0
                        val currentReservations = element["activeReservations"]?.toString()?.toIntOrNull() ?: 0
                        val currentOccupied = element["occupiedSpaces"]?.toString()?.toIntOrNull() ?: 0
                        
                        val newEarnings = currentEarnings + reservationPrice
                        val newReservations = currentReservations + 1
                        val newOccupied = currentOccupied + 1
                        
                        val newSummaryJson = """
                        {
                            "totalEarnings": $newEarnings,
                            "activeReservations": $newReservations,
                            "occupiedSpaces": $newOccupied,
                            "totalSpaces": ${element["totalSpaces"]?.toString() ?: "0"},
                            "pricePerHour": ${element["pricePerHour"]?.toString() ?: "{}"}
                        }
                        """.trimIndent()
                        firebaseManager.saveData("parkings/$parkingId/summary", newSummaryJson)
                    }
                } catch (e: Exception) {
                    println("Error actualizando summary: ${e.message}")
                }
            }
            
            val notificationData = """
            {
                "id": $resId,
                "title": "Nueva Reserva",
                "message": "El cliente $clientName ha reservado el espacio ${spaceDTO.number}",
                "timestamp": $startTime,
                "isUnread": true
            }
            """.trimIndent()
            firebaseManager.saveData("notifications/${parking.ownerId}/res_$startTime", notificationData)
        }
        return resId
    }
}