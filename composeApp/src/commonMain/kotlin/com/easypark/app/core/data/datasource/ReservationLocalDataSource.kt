package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.ReservationEntity

interface ReservationLocalDataSource {
    suspend fun create(entity: ReservationEntity)
    suspend fun readByStatus(status: String): List<ReservationEntity>
    suspend fun readByDriver(driverId: Int): List<ReservationEntity>
    suspend fun readByParking(parkingId: Int): List<ReservationEntity>
    suspend fun readById(id: Int): ReservationEntity?
    suspend fun readByQuery(query: String): List<ReservationEntity>
    suspend fun updateStatus(id: Int, isDone: Boolean): Boolean
    suspend fun deleteAll()
    suspend fun deleteById(id: Int)
}