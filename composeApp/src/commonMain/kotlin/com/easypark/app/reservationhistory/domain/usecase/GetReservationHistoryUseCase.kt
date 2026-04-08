package com.easypark.app.reservationhistory.domain.usecase

import com.easypark.app.reservationhistory.domain.model.ReservationItem
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetReservationHistoryUseCase(
    private val repository: ReservationHistoryRepository
) {
    operator fun invoke(): Flow<List<ReservationItem>> {
        return repository.getReservations()
    }
}
