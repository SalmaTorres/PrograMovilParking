package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.ReviewEntity

interface ReviewLocalDataSource {
    suspend fun save(entity: ReviewEntity)
    suspend fun listByParking(parkingId: Int): List<ReviewEntity>
    suspend fun countForParking(parkingId: Int): Int
}