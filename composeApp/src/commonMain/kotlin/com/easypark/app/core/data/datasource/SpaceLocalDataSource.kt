package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.SpaceEntity

interface SpaceLocalDataSource {
    suspend fun crateAll(entities: List<SpaceEntity>)
    suspend fun readById(id: Int): SpaceEntity?
    suspend fun readByParking(parkingId: Int): List<SpaceEntity>
    suspend fun updateStatus(spaceId: Int, isOccupied: Boolean)
}