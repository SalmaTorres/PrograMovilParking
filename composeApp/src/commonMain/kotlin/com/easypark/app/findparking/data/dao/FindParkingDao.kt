package com.easypark.app.findparking.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.ParkingEntity

@Dao
interface FindParkingDao {
    @Query("SELECT * FROM parking")
    suspend fun getAll(): List<ParkingEntity>
}