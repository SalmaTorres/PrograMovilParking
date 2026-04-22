package com.easypark.app.registerparking.data.repository

import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.data.mapper.toRemote
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registerparking.domain.repository.RegisterParkingRepository
import com.easypark.app.registerparking.data.datasource.RegisterParkingLocalDataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RegisterParkingRepositoryImpl (
    private val localDS: RegisterParkingLocalDataSource,
    private val firebaseManager: FirebaseManager
): RegisterParkingRepository {

    override suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Pair<Int, Int>? {
        return try {
            val userIdLong = localDS.saveUser(user.toEntity())
            val userId = userIdLong.toInt()

            val parkingIdLong = localDS.saveParking(parking.toEntity(ownerId = userId))
            val parkingId = parkingIdLong.toInt()

            val spacesToCreate = (1..parking.totalSpaces).map { i ->
                SpaceEntity(parkingId = parkingId, number = i, state = "LIBRE")
            }
            localDS.saveSpaces(spacesToCreate)

            val remoteParking = parking.toRemote(parkingId, userId)
            val jsonParking = Json.encodeToString(remoteParking)

            firebaseManager.saveData("parkings/$parkingId", jsonParking)

            firebaseManager.saveData("parkings/$parkingId/summary", """
                {
                    "totalEarnings": 0.0,
                    "activeReservations": 0,
                    "occupiedSpaces": 0,
                    "totalSpaces": ${parking.totalSpaces}
                }
            """.trimIndent())

            userId to parkingId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}