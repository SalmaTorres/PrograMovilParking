package com.easypark.app.registervehicle.data.repository

import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository

class RegisterVehicleRepositoryImpl(
    private val localDS: RegisterVehicleLocalDataSource
) : RegisterVehicleRepository {
    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Boolean {
        return try {
            val userId = localDS.saveUser(user.toEntity())
            val vehicleEntity = vehicle.toEntity().copy(driverId = userId)
            localDS.saveVehicle(vehicleEntity)
            true
        } catch (e: Exception) { false }
    }
}