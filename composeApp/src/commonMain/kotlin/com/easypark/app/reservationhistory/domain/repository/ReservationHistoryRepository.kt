package com.easypark.app.reservationhistory.domain.repository

import com.easypark.app.reservationhistory.domain.model.ReservationItem

interface ReservationHistoryRepository {
    suspend fun getReservations(): List<ReservationItem>
}
