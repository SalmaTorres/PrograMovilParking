package com.easypark.app.reservationhistory.domain.usecase

import com.easypark.app.reservationhistory.domain.model.ReservationItem
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository

class GetReservationHistoryUseCase(
    private val repository: ReservationHistoryRepository
) {
    suspend fun execute(): List<ReservationItem> {
        return repository.getReservations()
    }
}
