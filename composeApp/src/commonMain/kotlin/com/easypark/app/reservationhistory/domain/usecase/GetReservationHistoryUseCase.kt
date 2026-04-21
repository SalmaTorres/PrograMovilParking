package com.easypark.app.reservationhistory.domain.usecase

import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository

class GetReservationHistoryUseCase(
    private val repository: ReservationHistoryRepository
) {
    suspend operator fun invoke(userId: Int): List<ReservationItemModel> {
        return repository.getReservations(userId)
    }
}
