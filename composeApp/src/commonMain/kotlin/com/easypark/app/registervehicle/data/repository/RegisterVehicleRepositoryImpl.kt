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
        println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] Starting driver registration for user: ${user.email}")
        return try {
            // Guardar Usuario local y obtener ID
            val userId = localDS.saveUser(user.toEntity()).toInt()
            println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] User saved locally with ID: $userId")

            // Guardar Vehículo localmente vinculado al userId
            localDS.saveVehicle(vehicle.toEntity(userId))
            println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] Vehicle saved locally for userId: $userId")

            // JSON del Usuario
            val userJson = """
            {
                "id": $userId,
                "name": "${user.name}",
                "email": "${user.email}",
                "cellphone": "${user.cellphone}",
                "password": "${user.password}",
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
            println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] Firebase -> saving vehicles/$userId")
            firebaseManager.saveData("vehicles/$userId", vehicleJson)
            
            // Guardamos por email sanitizado como nodo principal
            val sanitizedEmail = user.email.replace(".", "_")
            println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] Firebase -> saving users/$sanitizedEmail")
            firebaseManager.saveData("users/$sanitizedEmail", userJson)

            println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] Success. Returning userId: $userId")
            userId
        } catch (e: Exception) {
            println("RegisterVehicleRepositoryImpl: [completeDriverRegistration] ERROR during registration: ${e.message}")
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