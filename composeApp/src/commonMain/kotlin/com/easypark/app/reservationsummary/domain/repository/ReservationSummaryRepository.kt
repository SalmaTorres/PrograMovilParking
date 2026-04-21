package com.easypark.app.reservationsummary.domain.repository

import ReservationModel

interface ReservationSummaryRepository {
    suspend fun getActiveReservations(userId: Int): List<ReservationModel>
}