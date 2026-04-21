package com.easypark.app.signin.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.UserEntity

interface SignInLocalDataSource {
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun getParkingByOwner(ownerId: Int): ParkingEntity?
}