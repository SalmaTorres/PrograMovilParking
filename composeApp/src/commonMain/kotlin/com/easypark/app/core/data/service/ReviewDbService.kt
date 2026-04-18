package com.easypark.app.core.data.service

import com.easypark.app.core.data.dao.ReviewDao
import com.easypark.app.core.data.datasource.ReviewLocalDataSource
import com.easypark.app.core.data.entity.ReviewEntity

class ReviewDbService(private val reviewDao: ReviewDao) : ReviewLocalDataSource {
    override suspend fun save(entity: ReviewEntity) {
        reviewDao.insert(entity)
    }

    override suspend fun listByParking(parkingId: Int): List<ReviewEntity> {
        return reviewDao.getByParking(parkingId)
    }

    override suspend fun countForParking(parkingId: Int): Int {
        return reviewDao.countByParking(parkingId)
    }
}