package com.easypark.app.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_config")
data class RemoteConfigEntity(
    @PrimaryKey
    @ColumnInfo(name = "config_key")
    val configKey: String,

    @ColumnInfo(name = "config_value")
    val configValue: String
)
