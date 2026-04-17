package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.VehicleEntity

@Dao
interface VehicleDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicle")
    suspend fun getList(): List<VehicleEntity>

    @Query("SELECT * FROM vehicle WHERE id = :id")
    suspend fun getById(id: String): VehicleEntity?

    @Query("DELETE FROM vehicle")
    suspend fun deleteAll()
}