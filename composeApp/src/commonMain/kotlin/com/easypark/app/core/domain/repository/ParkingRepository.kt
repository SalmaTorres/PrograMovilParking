package com.easypark.app.core.domain.repository

import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel

interface ParkingRepository {
    suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Boolean
}