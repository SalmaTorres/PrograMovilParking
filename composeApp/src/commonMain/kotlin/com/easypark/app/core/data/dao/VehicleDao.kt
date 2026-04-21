package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.VehicleEntity

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle WHERE id = :id")
    suspend fun getById(id: String): VehicleEntity?
}