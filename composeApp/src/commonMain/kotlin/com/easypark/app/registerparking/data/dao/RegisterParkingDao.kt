package com.easypark.app.registerparking.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.entity.UserEntity

@Dao
interface RegisterParkingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParking(parking: ParkingEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpaces(lists: List<SpaceEntity>)
}