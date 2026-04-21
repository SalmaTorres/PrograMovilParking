package com.easypark.app.registerparking.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.entity.UserEntity

interface RegisterParkingLocalDataSource {
    suspend fun saveUser(user: UserEntity): Int
    suspend fun saveParking(entity: ParkingEntity): Int
    suspend fun saveSpaces(entities: List<SpaceEntity>)
}