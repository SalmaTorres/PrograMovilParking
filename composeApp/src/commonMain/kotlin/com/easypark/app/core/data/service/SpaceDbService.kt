package com.easypark.app.core.data.service

import com.easypark.app.core.data.dao.SpaceDao
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.entity.SpaceEntity

class SpaceDbService(private val spaceDao: SpaceDao) : SpaceLocalDataSource {
    override suspend fun countTotal(id: Int): Int {
        return spaceDao.countTotal(id)
    }

    override suspend fun countAvailable(id: Int): Int {
        return spaceDao.countAvailable(id)
    }

    override suspend fun getById(spaceId: Int): SpaceEntity {
        return spaceDao.getById(spaceId) ?: throw Exception("Espacio no encontrado")
    }

    override suspend fun getMySpaces(parkingId: Int): List<SpaceEntity> {
        return spaceDao.getMySpaces(parkingId)
    }
}