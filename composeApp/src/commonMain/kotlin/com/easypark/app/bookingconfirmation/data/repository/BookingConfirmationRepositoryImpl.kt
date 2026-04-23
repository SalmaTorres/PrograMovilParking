package com.easypark.app.bookingconfirmation.data.repository

import ReservationModel
import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.data.sync.SyncManager
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class BookingConfirmationRepositoryImpl(
    private val bookingDS: BookingConfirmationLocalDataSource,
    private val notificationDS: NotificationLocalDataSource,
    private val firebaseManager: FirebaseManager,
    private val syncManager: SyncManager,
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
            } else { emptyMap() }
        } catch (e: Exception) { return null }

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
            methodPay = paymentMethod,
            isSynced = false
        )

        val resId = bookingDS.save(entity)

        if (resId != null) {
            syncManager.enqueueOfflineSync()
            try {
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

            } catch (e: Exception) {
            }
        }
        return resId?.toInt()
    }
}