package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM user")
    suspend fun getList(): List<UserEntity>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}