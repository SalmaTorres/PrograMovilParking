package com.easypark.app.registervehicle.data.repository

import com.easypark.app.core.data.dto.UserDTO
import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.data.mapper.toRemote
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RegisterVehicleRepositoryImpl(
    private val localDS: RegisterVehicleLocalDataSource,
    private val firebaseManager: FirebaseManager
) : RegisterVehicleRepository {
    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Int? {
        return try {
            val userId = localDS.saveUser(user.toEntity()).toInt()
            localDS.saveVehicle(vehicle.toEntity(driverId = userId))

            val userDto = UserDTO(
                id = userId,
                name = user.name,
                email = user.email,
                cellphone = user.cellphone,
                type = "DRIVER"
            )
            firebaseManager.saveData("users/$userId", Json.encodeToString(userDto))

            val vehicleRemote = vehicle.toRemote(driverId = userId)
            firebaseManager.saveData("vehicles/$userId", Json.encodeToString(vehicleRemote))

            userId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}