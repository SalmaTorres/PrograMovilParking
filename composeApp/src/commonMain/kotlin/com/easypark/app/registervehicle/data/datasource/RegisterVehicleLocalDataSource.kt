package com.easypark.app.registervehicle.data.datasource

import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.data.entity.VehicleEntity

interface RegisterVehicleLocalDataSource {
    suspend fun saveUser(entity: UserEntity): Int
    suspend fun saveVehicle(entity: VehicleEntity)
}