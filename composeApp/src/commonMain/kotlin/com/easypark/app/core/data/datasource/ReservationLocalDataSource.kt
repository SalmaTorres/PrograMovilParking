package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.ReservationEntity

interface ReservationLocalDataSource {
    suspend fun readByParking(parkingId: Int): List<ReservationEntity>
}