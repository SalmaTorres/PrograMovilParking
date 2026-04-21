package com.easypark.app.bookingconfirmation.data.repository

import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import kotlin.time.Clock

class BookingConfirmationRepositoryImpl(
    private val bookingDS: BookingConfirmationLocalDataSource,
    private val notificationDS: NotificationLocalDataSource
) : BookingConfirmationRepository {

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

        val reservationId = bookingDS.save(entity)

        if (reservationId != null) {
            bookingDS.updateSpaceStatus(space.id, "OCUPADO")

            val ownerId = bookingDS.getOwnerIdByParking(parkingId)
            if (ownerId != null) {
                notificationDS.create(
                    NotificationEntity(
                        userId = ownerId,
                        title = "¡Nueva Reserva!",
                        content = "Se ha reservado el espacio ${space.number} por $duration hora(s).",
                        state = "UNREAD",
                    )
                )
            }
        }

        return reservationId.toInt()
    }
}