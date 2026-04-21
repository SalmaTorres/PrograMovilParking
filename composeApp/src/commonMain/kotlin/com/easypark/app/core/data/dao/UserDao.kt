package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: Int): UserEntity?
}