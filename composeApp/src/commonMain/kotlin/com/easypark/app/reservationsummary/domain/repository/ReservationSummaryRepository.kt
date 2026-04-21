package com.easypark.app.reservationsummary.domain.repository

import com.easypark.app.core.domain.model.ReservationModel

interface ReservationSummaryRepository {
    suspend fun getReservationSummary(id: Int): ReservationModel
}
