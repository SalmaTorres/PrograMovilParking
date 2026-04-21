package com.easypark.app.bookingconfirmation.domain.usecase

import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository

class ConfirmReservationUseCase(private val repository: BookingConfirmationRepository) {
    suspend operator fun invoke(parkingId: Int, driverId: Int, duration: Int): Int? {
        return repository.makeReservation(parkingId, driverId, duration)
    }
}