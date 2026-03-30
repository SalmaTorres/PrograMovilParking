package com.easypark.app.bookingconfirmation.domain.usecase

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository

class GetBookingConfirmationUseCase(
    private val repository: BookingConfirmationRepository
) {
    suspend operator fun invoke(id: String): BookingConfirmation {
        return repository.getBookingConfirmation(id)
    }
}
