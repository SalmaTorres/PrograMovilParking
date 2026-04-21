package com.easypark.app.parkingdetails.domain.repository

import com.easypark.app.registerparking.domain.model.ParkingModel

interface ParkingDetailsRepository {
    suspend fun getParkingDetail(id: Int): ParkingModel
    suspend fun rateParking(parkingId: Int, userId: Int, rating: Float)
}