package com.easypark.app.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun getConfig(key: String): String?
    fun observeConfig(key: String): Flow<String?>
    suspend fun saveConfig(key: String, value: String)
}
