package com.easypark.app.registervehicle.domain.repository

import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel

interface RegisterVehicleRepository {
    suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Int?
}