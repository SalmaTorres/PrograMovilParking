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
        println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Starting owner registration for user: ${user.email}")
        return try {
            val userId = localDS.saveUser(user.toEntity()).toInt()
            val parkingId = localDS.saveParking(parking.toEntity(ownerId = userId)).toInt()
            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Saved locally. userId: $userId, parkingId: $parkingId")

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
                "rating": 0.0,
                "reviewCount": 0,
                "ownerId": $userId,
                "pricePerHour": {
                    "amount": ${parking.pricePerHour.amount},
                    "currency": "${parking.pricePerHour.currency}"
                }
            }
            """.trimIndent()
            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Firebase -> saving parkings/$parkingId")
            firebaseManager.saveData("parkings/$parkingId", parkingJson)

            // 2. Datos del Summary (PARA EARNINGS)
            val summaryJson = """
            {
                "totalEarnings": 0.0,
                "activeReservations": 0,
                "occupiedSpaces": 0,
                "totalSpaces": ${parking.totalSpaces},
                "pricePerHour": {
                    "amount": ${parking.pricePerHour.amount},
                    "currency": "${parking.pricePerHour.currency}"
                }
            }
            """.trimIndent()
            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Firebase -> saving parkings/$parkingId/summary")
            firebaseManager.saveData("parkings/$parkingId/summary", summaryJson)

            // 3. CREAR LOS ESPACIOS (PARA SPACE MANAGEMENT)
            val spacesJsonBuilder = StringBuilder("{")
            for (i in 1..parking.totalSpaces) {
                spacesJsonBuilder.append("""
                    "s$i": {
                        "id": $i,
                        "parkingId": $parkingId,
                        "number": $i,
                        "state": "LIBRE"
                    }
                """.trimIndent())
                if (i < parking.totalSpaces) {
                    spacesJsonBuilder.append(",")
                }
            }
            spacesJsonBuilder.append("}")
            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Firebase -> saving spaces/$parkingId")
            firebaseManager.saveData("spaces/$parkingId", spacesJsonBuilder.toString())

            // 4. Usuario con contraseña (incluyendo contraseña para que el login posterior funcione)
            val userJson = """
            {
                "id": $userId,
                "name": "${user.name}",
                "email": "${user.email}",
                "cellphone": "${user.cellphone}",
                "password": "${user.password}",
                "type": "OWNER",
                "placaVehiculo": "",
                "tipoVehiculo": ""
            }
            """.trimIndent()
            val sanitizedEmail = user.email.replace(".", "_")
            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Firebase -> saving users/$sanitizedEmail")
            firebaseManager.saveData("users/$sanitizedEmail", userJson)

            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] Success. Returning userId: $userId, parkingId: $parkingId")
            userId to parkingId
        } catch (e: Exception) {
            println("RegisterParkingRepositoryImpl: [completeOwnerRegistration] ERROR during registration: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}