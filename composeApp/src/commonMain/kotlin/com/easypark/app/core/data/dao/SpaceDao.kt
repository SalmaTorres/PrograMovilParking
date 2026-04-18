package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.SpaceEntity

@Dao
interface SpaceDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(space: SpaceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpaces(lists: List<SpaceEntity>)

    @Query("SELECT * FROM space")
    suspend fun getList(): List<SpaceEntity>

    @Query("SELECT * FROM space WHERE id = :id")
    suspend fun getById(id: Int): SpaceEntity?

    @Query("SELECT * FROM space WHERE parkingId = :parkingId")
    suspend fun getMySpaces(parkingId: Int): List<SpaceEntity>

    @Query("UPDATE space SET state = :newState WHERE id = :id")
    suspend fun updateStatus(id: Int, newState: String)

    @Query("DELETE FROM space")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM space WHERE parkingId = :parkingId")
    suspend fun countTotal(parkingId: Int): Int

    @Query("SELECT COUNT(*) FROM space WHERE parkingId = :parkingId AND state = 'LIBRE'")
    suspend fun countAvailable(parkingId: Int): Int
}