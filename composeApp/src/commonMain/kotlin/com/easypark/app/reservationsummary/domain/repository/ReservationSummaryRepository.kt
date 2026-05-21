package com.easypark.app.reservationsummary.domain.repository

import ReservationModel
import kotlinx.coroutines.flow.Flow

interface ReservationSummaryRepository {
    suspend fun getActiveReservations(userId: Int): List<ReservationModel>
    fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>>
    suspend fun checkInReservation(reservationId: Int, parkingId: Int, spaceId: Int)
    suspend fun checkOutReservation(reservationId: Int, parkingId: Int, spaceId: Int)
    suspend fun checkAndEvacuateExpiredReservations()
}