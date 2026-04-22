package com.easypark.app.reservationsummary.domain.repository

import ReservationModel
import kotlinx.coroutines.flow.Flow

interface ReservationSummaryRepository {
    suspend fun getActiveReservations(userId: Int): List<ReservationModel>
    fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>>
}