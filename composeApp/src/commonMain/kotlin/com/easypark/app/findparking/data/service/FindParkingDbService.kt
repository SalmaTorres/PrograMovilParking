package com.easypark.app.findparking.data.service

import com.easypark.app.findparking.data.dao.FindParkingDao
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.findparking.data.datasource.FindParkingLocalDataSource

class FindParkingDbService(
    private val findParkingDao: FindParkingDao
) : FindParkingLocalDataSource {
    override suspend fun getAllParkings(): List<ParkingEntity> = findParkingDao.getAll()
}