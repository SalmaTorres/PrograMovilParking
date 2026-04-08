package com.easypark.app.reservationhistory.domain.repository

import com.easypark.app.reservationhistory.domain.model.ReservationItem
import kotlinx.coroutines.flow.Flow

interface ReservationHistoryRepository {
    fun getReservations(): Flow<List<ReservationItem>>
}
