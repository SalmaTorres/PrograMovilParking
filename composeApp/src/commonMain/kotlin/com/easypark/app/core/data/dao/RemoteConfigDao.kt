package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.RemoteConfigEntity

@Dao
interface RemoteConfigDao {
    @Query("SELECT * FROM remote_config WHERE config_key = :key LIMIT 1")
    suspend fun getConfig(key: String): RemoteConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: RemoteConfigEntity)
}
