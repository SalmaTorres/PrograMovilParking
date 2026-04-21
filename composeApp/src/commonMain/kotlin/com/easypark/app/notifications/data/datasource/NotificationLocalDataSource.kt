package com.easypark.app.notifications.data.datasource

import com.easypark.app.core.data.entity.NotificationEntity

interface NotificationLocalDataSource {
    suspend fun create(entity: NotificationEntity)
    suspend fun readById(id: Int): NotificationEntity?

    suspend fun readAll(userId: Int): List<NotificationEntity>

    suspend fun updateStatus(id: Int, isViewed: Boolean): Boolean
    suspend fun deleteById(id: Int)

    suspend fun deleteAll(userId: Int)
}