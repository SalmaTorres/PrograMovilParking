package com.easypark.app.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_config")
data class RemoteConfigEntity(
    @PrimaryKey
    val configKey: String,
    val configValue: String
)
