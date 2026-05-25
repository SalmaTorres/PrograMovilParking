package com.easypark.app.reservationsummary.domain.usecase

import ReservationModel
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository

open class GetReservationSummaryUseCase(
    private val repository: ReservationSummaryRepository
) {
    open suspend operator fun invoke(userId: Int) = repository.getActiveReservations(userId)

    open fun observe(userId: Int) = repository.observeActiveReservations(userId)

    open suspend fun checkIn(reservationId: Int, parkingId: Int, spaceId: Int) {
        repository.checkInReservation(reservationId, parkingId, spaceId)
    }

    open suspend fun checkOut(reservationId: Int, parkingId: Int, spaceId: Int) {
        repository.checkOutReservation(reservationId, parkingId, spaceId)
    }

    open suspend fun evacuate() {
        repository.checkAndEvacuateExpiredReservations()
    }

    open suspend fun cancelExpiredReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
        repository.checkAndCancelReservation(reservationId, parkingId, spaceId)
    }
}