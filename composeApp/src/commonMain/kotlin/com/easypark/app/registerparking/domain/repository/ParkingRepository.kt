package com.easypark.app.registerparking.domain.repository

import com.easypark.app.registerparking.domain.model.ParkingModel

interface ParkingRepository {
    suspend fun registerParking(parking: ParkingModel): Boolean
}