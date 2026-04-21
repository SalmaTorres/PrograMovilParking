package com.easypark.app.reservationsummary.domain.usecase

import com.easypark.app.core.domain.model.ReservationModel
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository

class GetReservationSummaryUseCase(
    private val repository: ReservationSummaryRepository
) {
    suspend operator fun invoke(reservationId: Int): List<ReservationModel> {
        return repository.getActiveReservations(reservationId)
    }
}