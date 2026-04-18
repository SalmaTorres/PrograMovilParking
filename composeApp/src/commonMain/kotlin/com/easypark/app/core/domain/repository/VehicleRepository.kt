package com.easypark.app.core.domain.repository

import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.VehicleModel

interface VehicleRepository {
    suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Boolean
}