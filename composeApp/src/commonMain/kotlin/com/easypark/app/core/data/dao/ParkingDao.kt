package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.ParkingEntity

@Dao
interface ParkingDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(parking: ParkingEntity): Long

    @Query("SELECT * FROM parking")
    suspend fun getAll(): List<ParkingEntity>

    @Query("SELECT * FROM parking WHERE id = :id")
    suspend fun getById(id: Int): ParkingEntity?

    @Query("UPDATE parking SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Float)

    @Query("DELETE FROM parking")
    suspend fun deleteAll()
}