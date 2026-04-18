package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.VehicleEntity

interface VehicleLocalDataSource {
    suspend fun save(entity: VehicleEntity)
}