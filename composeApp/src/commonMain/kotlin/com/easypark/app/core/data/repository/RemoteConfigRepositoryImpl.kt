package com.easypark.app.core.data.repository

import com.easypark.app.core.data.dao.RemoteConfigDao
import com.easypark.app.core.data.entity.RemoteConfigEntity
import com.easypark.app.core.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteConfigRepositoryImpl(
    private val dao: RemoteConfigDao
) : RemoteConfigRepository {
    override suspend fun getConfig(key: String): String? {
        return dao.getConfig(key)?.configValue
    }

    override fun observeConfig(key: String): Flow<String?> {
        return dao.observeConfig(key).map { it?.configValue }
    }

    override suspend fun saveConfig(key: String, value: String) {
        dao.insertOrReplace(RemoteConfigEntity(configKey = key, configValue = value))
    }
}
