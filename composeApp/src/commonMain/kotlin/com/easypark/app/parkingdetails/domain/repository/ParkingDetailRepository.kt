package com.easypark.app.parkingdetails.domain.repository

import com.easypark.app.parkingdetails.domain.model.ParkingDetail

interface ParkingDetailRepository {
    suspend fun getParkingDetail(id: String): ParkingDetail
}
