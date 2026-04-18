package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity

interface ParkingLocalDataSource {
    suspend fun save(entity: ParkingEntity)
    suspend fun readByQuery(query: String): List<ParkingEntity>
    suspend fun readById(id: Int): ParkingEntity?
    suspend fun readAll(): List<ParkingEntity>
}