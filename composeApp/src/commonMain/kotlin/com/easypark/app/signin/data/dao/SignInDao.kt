package com.easypark.app.signin.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.UserEntity

@Dao
interface SignInDao {
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?
    @Query("SELECT * FROM parking WHERE ownerId = :ownerId LIMIT 1")
    suspend fun getParkingByOwner(ownerId: Int): ParkingEntity?
}