package com.easypark.app.bookingconfirmation.domain.usecase

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation
import com.easypark.app.shared.domain.repository.ParkingRepository

class GetBookingConfirmationUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(id: String): BookingConfirmation {
        return repository.getBookingConfirmation(id)
    }
}
