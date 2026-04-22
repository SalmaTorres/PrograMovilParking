package com.easypark.app.bookingconfirmation.domain.repository

import ReservationModel
import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import kotlinx.coroutines.flow.Flow

interface BookingConfirmationRepository {
    suspend fun getBookingInfo(parkingId: Int): BookingConfirmationModel
    suspend fun makeReservation(parkingId: Int, driverId: Int, duration: Int, paymentMethod: String, clientName: String): Int?
    suspend fun observeBookingRealtime(bookingId: String): Flow<ReservationModel?>
}