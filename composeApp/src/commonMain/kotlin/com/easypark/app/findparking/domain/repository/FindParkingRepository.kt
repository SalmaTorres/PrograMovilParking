package com.easypark.app.findparking.domain.repository

import com.easypark.app.registerparking.domain.model.ParkingModel
import kotlinx.coroutines.flow.Flow

interface FindParkingRepository {
    suspend fun getAvailableParkings(): List<ParkingModel>
    fun observeParkingsRealtime(): Flow<List<ParkingModel>>
}