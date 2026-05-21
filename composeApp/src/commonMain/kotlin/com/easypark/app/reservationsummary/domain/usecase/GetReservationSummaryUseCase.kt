package com.easypark.app.reservationsummary.domain.usecase

import ReservationModel
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository

class GetReservationSummaryUseCase(
    private val repository: ReservationSummaryRepository
) {
    suspend operator fun invoke(userId: Int) = repository.getActiveReservations(userId)

    fun observe(userId: Int) = repository.observeActiveReservations(userId)

    suspend fun checkIn(reservationId: Int, parkingId: Int, spaceId: Int) {
        repository.checkInReservation(reservationId, parkingId, spaceId)
    }

    suspend fun checkOut(reservationId: Int, parkingId: Int, spaceId: Int) {
        repository.checkOutReservation(reservationId, parkingId, spaceId)
    }

    suspend fun evacuate() {
        repository.checkAndEvacuateExpiredReservations()
    }
}