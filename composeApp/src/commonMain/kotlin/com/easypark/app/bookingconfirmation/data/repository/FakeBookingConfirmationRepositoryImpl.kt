package com.easypark.app.bookingconfirmation.data.repository

import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmation
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import kotlinx.coroutines.delay

class FakeBookingConfirmationRepositoryImpl : BookingConfirmationRepository {
    override suspend fun getBookingConfirmation(id: String): BookingConfirmation {
        delay(500)
        return BookingConfirmation(
            locationName = "Estacionamiento Central",
            address = "Av. Principal 123",
            spaceIdentifier = "A - 12",
            durationText = "2 Horas",
            totalCostText = "$ 5.00"
        )
    }
}
