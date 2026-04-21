package com.easypark.app.registervehicle.data.repository

import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository

class RegisterVehicleRepositoryImpl(
    private val localDS: RegisterVehicleLocalDataSource
) : RegisterVehicleRepository {
    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Int? {
        return try {
            val userId = localDS.saveUser(user.toEntity()).toInt()
            localDS.saveVehicle(vehicle.toEntity(driverId = userId)) // El cambio del punto 1

            userId
        } catch (e: Exception) {
            null
        }
    }
}