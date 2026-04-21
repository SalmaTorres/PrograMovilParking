package com.easypark.app.bookingconfirmation.domain.usecase

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository

class GetBookingInfoUseCase(private val repository: BookingConfirmationRepository) {
    suspend operator fun invoke(parkingId: Int): BookingConfirmationModel {
        return repository.getBookingInfo(parkingId)
    }
}