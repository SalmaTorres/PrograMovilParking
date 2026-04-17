package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.NotificationEntity

@Dao
interface NotificationDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notification")
    suspend fun getList(): List<NotificationEntity>

    @Query("SELECT * FROM notification WHERE id = :id")
    suspend fun getById(id: String): NotificationEntity?

    @Query("UPDATE notification SET state = :newState WHERE id = :id")
    suspend fun updateStatus(id: Int, newState: String)

    @Query("DELETE FROM notification")
    suspend fun deleteAll()
}