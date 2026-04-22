package com.easypark.app.bookingconfirmation.data.repository

import ReservationModel
import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class BookingConfirmationRepositoryImpl(
    private val bookingDS: BookingConfirmationLocalDataSource,
    private val notificationDS: NotificationLocalDataSource,
    private val firebaseManager: FirebaseManager
) : BookingConfirmationRepository {
    override suspend fun observeBookingRealtime(bookingId: String): Flow<ReservationModel?> {
        return firebaseManager.observeData("reservations/$bookingId").map { json ->
            if (json == null) return@map null

            val dto = Json.decodeFromString<ReservationDTO>(json)
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

    override suspend fun makeReservation(parkingId: Int, driverId: Int, duration: Int): Int? {
        val space = bookingDS.getFirstAvailableSpace(parkingId) ?: return null
        val parking = bookingDS.getParkingById(parkingId) ?: return null

        val startTime = Clock.System.now().toEpochMilliseconds()
        val entity = ReservationEntity(
            spaceId = space.id,
            driverId = driverId,
            startHour = startTime,
            finalHour = startTime + (duration * 3600000L),
            totalPrice = parking.pricePerHour * duration,
            state = "ACTIVE",
            methodPay = "CASH"
        )

        val resId = bookingDS.save(entity)

        if (resId != null) {
            val firebaseData = """
            {
                "id": $resId,
                "status": "ACTIVE",
                "parkingName": "${parking.name}",
                "totalPrice": { "amount": ${parking.pricePerHour * duration}, "currency": "BOB" }
            }
        """.trimIndent()

            firebaseManager.saveData("reservations/$resId", firebaseData)
        }
        return resId
    }
}