package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.SpaceEntity

interface SpaceLocalDataSource {
    suspend fun saveList(entities: List<SpaceEntity>)
    suspend fun countTotal(id: Int): Int
    suspend fun countAvailable(id: Int): Int
}