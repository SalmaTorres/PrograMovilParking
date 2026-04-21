package com.easypark.app.registervehicle.data.service

import com.easypark.app.registervehicle.data.dao.RegisterVehicleDao
import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.data.entity.VehicleEntity
import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource

class RegisterVehicleDbService(private val dao: RegisterVehicleDao) : RegisterVehicleLocalDataSource {
    override suspend fun saveUser(entity: UserEntity): Int = dao.insertUser(entity).toInt()
    override suspend fun saveVehicle(entity: VehicleEntity) = dao.insertVehicle(entity)
}