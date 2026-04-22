package com.easypark.app.registerparking.data.repository

import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registerparking.domain.repository.RegisterParkingRepository
import com.easypark.app.registerparking.data.datasource.RegisterParkingLocalDataSource

class RegisterParkingRepositoryImpl (
    private val localDS: RegisterParkingLocalDataSource,
    private val firebaseManager: FirebaseManager
): RegisterParkingRepository {

    override suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Pair<Int, Int>? {
        val userId = localDS.saveUser(user.toEntity()).toInt()
        val parkingId = localDS.saveParking(parking.toEntity(ownerId = userId)).toInt()

        // 1. Datos del Parqueo
        val parkingJson = """
        {
            "id": $parkingId,
            "name": "${parking.name}",
            "address": "${parking.address}",
            "latitude": ${parking.latitude},
            "longitude": ${parking.longitude},
            "availableSpaces": ${parking.totalSpaces},
            "totalSpaces": ${parking.totalSpaces},
            "rating": 4.5,
            "ownerId": $userId
        }
    """.trimIndent()
        firebaseManager.saveData("parkings/$parkingId", parkingJson)

        // 2. Datos del Summary (PARA EARNINGS)
        val summaryJson = """
        {
            "totalEarnings": 0.0,
            "activeReservations": 0,
            "occupiedSpaces": 0,
            "totalSpaces": ${parking.totalSpaces}
        }
    """.trimIndent()
        firebaseManager.saveData("parkings/$parkingId/summary", summaryJson)

        // 3. CREAR LOS ESPACIOS (PARA SPACE MANAGEMENT) - ¡ESTO FALTABA!
        val spaces = mutableMapOf<String, String>()
        for (i in 1..parking.totalSpaces) {
            val spaceJson = """
            {
                "id": $i,
                "parkingId": $parkingId,
                "number": $i,
                "state": "LIBRE"
            }
        """.trimIndent()
            firebaseManager.saveData("spaces/$parkingId/s$i", spaceJson)
        }

        // 4. Usuario
        val userJson = """
        {
            "id": $userId,
            "name": "${user.name}",
            "email": "${user.email}",
            "type": "OWNER"
        }
    """.trimIndent()
        firebaseManager.saveData("users/$userId", userJson)

        return userId to parkingId
    }
}