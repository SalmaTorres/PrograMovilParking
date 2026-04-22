package com.easypark.app.reservationhistory.domain.repository

import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import kotlinx.coroutines.flow.Flow

interface ReservationHistoryRepository {
    suspend fun getReservations(userId: Int): List<ReservationItemModel>
    fun observeReservationsRealtime(parkingId: Int): Flow<List<ReservationItemModel>>
}