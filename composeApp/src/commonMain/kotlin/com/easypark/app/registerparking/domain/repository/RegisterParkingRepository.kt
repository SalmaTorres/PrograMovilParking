package com.easypark.app.registerparking.domain.repository

import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel

interface RegisterParkingRepository {
    suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Boolean
}