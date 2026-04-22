package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.RemoteConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RemoteConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(config: RemoteConfigEntity)

    @Query("SELECT * FROM remote_config WHERE configKey = :key")
    suspend fun getConfig(key: String): RemoteConfigEntity?

    @Query("SELECT * FROM remote_config WHERE configKey = :key")
    fun observeConfig(key: String): Flow<RemoteConfigEntity?>
}
