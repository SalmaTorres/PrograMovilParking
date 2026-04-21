package com.easypark.app.notifications.data.repository

import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import com.easypark.app.notifications.domain.model.NotificationModel
import com.easypark.app.notifications.domain.repository.NotificationsRepository
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_calendar
import kotlinproject.composeapp.generated.resources.ic_notification

class NotificationsRepositoryImpl(
    private val localDS: NotificationLocalDataSource
) : NotificationsRepository {

    override suspend fun getNotifications(userId: Int): List<NotificationModel> {
        val entities = localDS.readAll(userId)

        return entities.map { entity ->
            NotificationModel(
                id = entity.id,
                title = entity.title,
                description = entity.content,
                time = "Hace un momento",
                icon = if (entity.state == "RESERVATION") Res.drawable.ic_calendar else Res.drawable.ic_notification,
                isUnread = entity.state == "UNREAD"
            )
        }
    }
}