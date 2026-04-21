package com.easypark.app.notifications.domain.usecase

import com.easypark.app.notifications.domain.repository.NotificationsRepository

class GetNotificationsUseCase(private val repository: NotificationsRepository) {
    suspend operator fun invoke(userId: Int) = repository.getNotifications(userId)
}