package com.easypark.app.findparking.data.datasource

import com.easypark.app.core.data.entity.ParkingEntity

interface FindParkingLocalDataSource {
    suspend fun getAllParkings(): List<ParkingEntity>
}