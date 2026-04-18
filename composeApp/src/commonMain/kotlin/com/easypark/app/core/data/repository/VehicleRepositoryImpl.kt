package com.easypark.app.core.data.repository

import com.easypark.app.core.data.datasource.UserLocalDataSource
import com.easypark.app.core.data.datasource.VehicleLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.VehicleModel
import com.easypark.app.core.domain.repository.VehicleRepository

class VehicleRepositoryImpl(
    private val userDS: UserLocalDataSource,
    private val vehicleDS: VehicleLocalDataSource
) : VehicleRepository {
    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Boolean {
        return try {
            val userId = userDS.save(user.toEntity())

            val vehicleEntity = vehicle.toEntity().copy(driverId = userId)
            vehicleDS.save(vehicleEntity)

            true
        } catch (e: Exception) {
            false
        }
    }
}