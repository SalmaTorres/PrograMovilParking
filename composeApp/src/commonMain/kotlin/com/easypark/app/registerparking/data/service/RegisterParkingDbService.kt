package com.easypark.app.registerparking.data.service

import com.easypark.app.registerparking.data.dao.RegisterParkingDao
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.registerparking.data.datasource.RegisterParkingLocalDataSource

class RegisterParkingDbService(
    private val registerDao: RegisterParkingDao,
) : RegisterParkingLocalDataSource {
    override suspend fun saveUser(user: UserEntity): Int = registerDao.insertUser(user).toInt()
    override suspend fun saveParking(entity: ParkingEntity): Int = registerDao.insertParking(entity).toInt()
    override suspend fun saveSpaces(entities: List<SpaceEntity>) = registerDao.insertSpaces(entities)
}