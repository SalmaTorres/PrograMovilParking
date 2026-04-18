package com.easypark.app.core.data.service

import com.easypark.app.core.data.dao.ParkingDao
import com.easypark.app.core.data.datasource.ParkingLocalDataSource
import com.easypark.app.core.data.entity.ParkingEntity

class ParkingDbService(private val parkingDao: ParkingDao) : ParkingLocalDataSource {
    override suspend fun save(entity: ParkingEntity): Int {
        return parkingDao.insert(entity).toInt()
    }

    override suspend fun readAll(): List<ParkingEntity> {
        return parkingDao.getAll()
    }

    override suspend fun readById(id: Int): ParkingEntity? {
        return parkingDao.getById(id)
    }
}