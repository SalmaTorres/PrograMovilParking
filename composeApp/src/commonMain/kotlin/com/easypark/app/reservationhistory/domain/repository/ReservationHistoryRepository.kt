package com.easypark.app.reservationhistory.domain.repository

import com.easypark.app.reservationhistory.domain.model.ReservationItemModel

interface ReservationHistoryRepository {
    suspend fun getReservations(userId: Int): List<ReservationItemModel>
}