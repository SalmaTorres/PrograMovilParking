package com.easypark.app.core.data.repository

import com.easypark.app.core.data.dao.RemoteConfigDao
import com.easypark.app.core.data.entity.RemoteConfigEntity
import com.easypark.app.core.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow

class RemoteConfigRepositoryImpl(
    private val dao: RemoteConfigDao
) : RemoteConfigRepository {

    override suspend fun saveConfig(key: String, value: String) {
        dao.insertConfig(RemoteConfigEntity(configKey = key, configValue = value))
    }

    override suspend fun getConfig(key: String): String? {
        return dao.getConfig(key)?.configValue
    }

    override fun observeConfig(key: String): Flow<String?> {
        return dao.getConfigFlow(key)
    }
}
