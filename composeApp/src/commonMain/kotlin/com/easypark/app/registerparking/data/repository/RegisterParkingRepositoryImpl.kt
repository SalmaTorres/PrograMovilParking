package com.easypark.app.registerparking.data.repository

import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registerparking.domain.repository.RegisterParkingRepository
import com.easypark.app.registerparking.data.datasource.RegisterParkingLocalDataSource

class RegisterParkingRepositoryImpl (
    private val localDS: RegisterParkingLocalDataSource
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

            userId to parkingId
        } catch (e: Exception) {
            null
        }
    }
}