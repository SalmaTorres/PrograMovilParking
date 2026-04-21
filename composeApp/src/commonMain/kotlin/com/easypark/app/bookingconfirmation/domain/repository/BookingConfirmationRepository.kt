package com.easypark.app.bookingconfirmation.domain.repository

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel

interface BookingConfirmationRepository {
    suspend fun getBookingInfo(parkingId: Int): BookingConfirmationModel
    suspend fun makeReservation(parkingId: Int, driverId: Int, duration: Int): Int?
}