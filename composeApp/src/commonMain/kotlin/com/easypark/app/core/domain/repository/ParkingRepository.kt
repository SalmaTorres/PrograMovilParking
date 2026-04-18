package com.easypark.app.core.domain.repository

import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel

interface ParkingRepository {
    suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Boolean
    suspend fun getAvailableParkings(): List<ParkingModel>
    suspend fun getParkingDetail(id: Int): ParkingModel
}