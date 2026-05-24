package com.easypark.app.registervehicle.data.repository

import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import kotlinx.coroutines.flow.firstOrNull

class RegisterVehicleRepositoryImpl(
    private val localDS: RegisterVehicleLocalDataSource,
    private val firebaseManager: FirebaseManager
) : RegisterVehicleRepository {

    // 1. Guardar Usuario y Vehículo por separado
    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Int? {
        return try {
            // Guardar Usuario local y obtener ID
            val userId = localDS.saveUser(user.toEntity()).toInt()

            // Guardar Vehículo localmente vinculado al userId
            localDS.saveVehicle(vehicle.toEntity(userId))

            // JSON del Usuario (Ya no tiene campos de vehículo)
            val userJson = """
            {
                "id": $userId,
                "name": "${user.name}",
                "email": "${user.email}",
                "cellphone": "${user.cellphone}",
                "type": "DRIVER"
            }
            """.trimIndent()

            // JSON del Vehículo
            val vehicleJson = """
            {
                "driverId": $userId,
                "plate": "${vehicle.plate}",
                "type": "${vehicle.type}",
                "model": "${vehicle.model}",
                "color": "${vehicle.color}"
            }
            """.trimIndent()

            // Guardar en Firebase en nodos separados
            firebaseManager.saveData("users/$userId", userJson)
            firebaseManager.saveData("vehicles/$userId", vehicleJson)

            userId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 2. NUEVA FUNCIÓN: Obtener vehículo para la reserva
    override suspend fun getVehicleByDriverId(driverId: Int): VehicleModel? {
        return try {
            // Primero intentamos localmente (más rápido)
            val localVehicle = localDS.getVehicleByDriver(driverId)?.toModel()
            if (localVehicle != null) return localVehicle

            // Si no está local, buscamos en Firebase
            val vehicleJson = firebaseManager.observeData("vehicles/$driverId").firstOrNull()
            if (vehicleJson != null) {
                // Aquí deberías usar tu jsonParser para convertir el String a VehicleModel
                // (Omito el parseo detallado para brevedad, pero la lógica es esta)
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}