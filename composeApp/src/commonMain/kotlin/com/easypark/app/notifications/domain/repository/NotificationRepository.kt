package com.easypark.app.notifications.domain.repository

import com.easypark.app.notifications.domain.model.NotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    suspend fun getNotifications(userId: Int): List<NotificationModel>
    suspend fun observeNotificationsRealtime(userId: Int): Flow<List<NotificationModel>>
}