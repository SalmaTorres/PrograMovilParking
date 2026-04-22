package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.easypark.app.core.data.entity.AppEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppEventDao {
    @Insert
    suspend fun insertEvent(event: AppEventEntity)

    @Query("SELECT * FROM app_events WHERE synced = 0")
    suspend fun getUnsyncedEvents(): List<AppEventEntity>

    @Update
    suspend fun updateEvents(events: List<AppEventEntity>)

    @Query("SELECT COUNT(*) FROM app_events")
    suspend fun getEventCount(): Int

    @Query("DELETE FROM app_events WHERE id IN (SELECT id FROM app_events ORDER BY timestamp ASC LIMIT :limit)")
    suspend fun deleteOldestEvents(limit: Int)
}
