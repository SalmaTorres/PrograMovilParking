package com.easypark.app.notifications.domain.repository

import com.easypark.app.notifications.domain.model.NotificationModel

interface NotificationsRepository {
    suspend fun getNotifications(userId: Int): List<NotificationModel>
}