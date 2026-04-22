package com.easypark.app.register.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.UserEntity

@Dao
interface RegisterDao {
    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    suspend fun countEmail(email: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long
}
