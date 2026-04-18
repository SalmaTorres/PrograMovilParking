package com.easypark.app.core.data.service

import com.easypark.app.core.data.dao.SpaceDao
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.entity.SpaceEntity

class SpaceDbService(private val spaceDao: SpaceDao) : SpaceLocalDataSource {

    override suspend fun saveList(entities: List<SpaceEntity>) {
        spaceDao.insertSpaces(entities)
    }

    override suspend fun countTotal(id: Int): Int {
        return spaceDao.countTotal(id)
    }

    override suspend fun countAvailable(id: Int): Int {
        return spaceDao.countAvailable(id)
    }
}