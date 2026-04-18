package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    suspend fun countEmail(email: String): Int
}