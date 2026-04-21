package com.easypark.app.findparking.domain.repository

import com.easypark.app.registerparking.domain.model.ParkingModel

interface FindParkingRepository {
    suspend fun getAvailableParkings(): List<ParkingModel>
}