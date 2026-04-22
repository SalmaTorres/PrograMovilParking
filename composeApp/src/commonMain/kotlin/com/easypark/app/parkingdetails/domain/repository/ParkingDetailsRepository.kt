package com.easypark.app.parkingdetails.domain.repository

import com.easypark.app.registerparking.domain.model.ParkingModel
import kotlinx.coroutines.flow.Flow

interface ParkingDetailsRepository {
    suspend fun getParkingDetail(id: Int): ParkingModel
    suspend fun rateParking(parkingId: Int, userId: Int, rating: Float)
    fun observeParkingDetail(id: Int): Flow<ParkingModel?>
}