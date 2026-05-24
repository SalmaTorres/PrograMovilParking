package com.easypark.app.registervehicle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.data.entity.VehicleEntity

@Dao
interface RegisterVehicleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicle WHERE driverId = :driverId LIMIT 1")
    suspend fun getVehicleByDriver(driverId: Int): VehicleEntity?
}