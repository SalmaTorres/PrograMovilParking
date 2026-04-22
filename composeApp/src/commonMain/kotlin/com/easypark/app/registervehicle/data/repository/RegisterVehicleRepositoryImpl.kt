package com.easypark.app.registervehicle.data.repository

import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository

class RegisterVehicleRepositoryImpl(
    private val localDS: RegisterVehicleLocalDataSource,
    private val firebaseManager: FirebaseManager
) : RegisterVehicleRepository {
    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Int? {
        return try {
            val userId = localDS.saveUser(user.toEntity()).toInt()
            localDS.saveVehicle(vehicle.toEntity(driverId = userId))

            val userJson = """
            {
                "id": $userId,
                "name": "${user.name}",
                "email": "${user.email}",
                "cellphone": "${user.cellphone}",
                "type": "DRIVER"
            }
            """.trimIndent()

            firebaseManager.saveData("users/$userId", userJson)

            val vehicleJson = """
            {
                "driverId": $userId,
                "plate": "${vehicle.plate}",
                "model": "${vehicle.model}",
                "color": "${vehicle.color}"
            }
            """.trimIndent()

            firebaseManager.saveData("vehicles/$userId", vehicleJson)

            userId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}