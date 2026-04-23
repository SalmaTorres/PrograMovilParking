package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.easypark.app.core.data.entity.AppEventEntity

@Dao
interface AppEventDao {
    @Insert
    suspend fun insert(event: AppEventEntity)

    @Query("SELECT * FROM app_event")
    suspend fun getAll(): List<AppEventEntity>

    @Query("DELETE FROM app_event WHERE id = :id")
    suspend fun deleteById(id: Int)
}