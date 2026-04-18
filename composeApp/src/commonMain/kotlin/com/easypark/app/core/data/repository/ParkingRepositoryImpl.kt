package com.easypark.app.core.data.repository

import com.easypark.app.core.data.datasource.ParkingLocalDataSource
import com.easypark.app.core.data.datasource.UserLocalDataSource
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.repository.ParkingRepository

class ParkingRepositoryImpl(
    private val userDS: UserLocalDataSource,
    private val parkingDS: ParkingLocalDataSource
) : ParkingRepository {
    override suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Boolean {
        return try {
            val userId = userDS.save(user.toEntity())

            val parkingEntity = parking.toEntity().copy(ownerId = userId)
            parkingDS.save(parkingEntity)

            true
        } catch (e: Exception) {
            false
        }
    }
}