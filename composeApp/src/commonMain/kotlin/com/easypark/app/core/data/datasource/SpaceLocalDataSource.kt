package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.SpaceEntity

interface SpaceLocalDataSource {
    suspend fun countTotal(id: Int): Int
    suspend fun countAvailable(id: Int): Int
    suspend fun getById(spaceId: Int): SpaceEntity
    suspend fun getMySpaces(parkingId: Int): List<SpaceEntity>
}