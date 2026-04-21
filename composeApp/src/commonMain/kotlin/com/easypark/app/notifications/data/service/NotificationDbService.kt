package com.easypark.app.notifications.data.service

import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.notifications.data.dao.NotificationDao
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource

class NotificationDbService(
    private val dao: NotificationDao
) : NotificationLocalDataSource {

    override suspend fun create(entity: NotificationEntity) = dao.insert(entity)

    override suspend fun readAll(userId: Int) = dao.getListByUser(userId)

    override suspend fun updateStatus(id: Int, isViewed: Boolean): Boolean {
        dao.updateStatus(id, if (isViewed) "READ" else "UNREAD")
        return true
    }

    override suspend fun readById(id: Int) = dao.getById(id.toString())

    override suspend fun deleteById(id: Int) { /* implementar si es necesario */ }

    override suspend fun deleteAll(userId: Int) = dao.deleteAll()
}