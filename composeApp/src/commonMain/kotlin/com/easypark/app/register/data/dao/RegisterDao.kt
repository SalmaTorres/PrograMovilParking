package com.easypark.app.register.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RegisterDao {
    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    suspend fun countEmail(email: String): Int
}