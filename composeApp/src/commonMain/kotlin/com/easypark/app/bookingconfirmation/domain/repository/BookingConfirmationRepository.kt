package com.easypark.app.bookingconfirmation.domain.repository

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation

interface BookingConfirmationRepository {
    suspend fun getBookingConfirmation(id: String): BookingConfirmation
}
