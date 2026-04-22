package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.notifications.data.dto.NotificationDTO
import com.easypark.app.notifications.domain.model.NotificationModel
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_notification

fun NotificationModel.toEntity(userId: Int) = NotificationEntity(
    userId = userId,
    title = this.title,
    content = this.description,
    state = if (this.isUnread) "UNREAD" else "READ"
).apply {
    this.id = id
}

fun NotificationEntity.toModel() = NotificationModel(
    id = this.id,
    title = this.title,
    description = this.content,
    time = "Hace un momento",
    icon = Res.drawable.ic_notification,
    isUnread = this.state == "UNREAD"
)

fun NotificationDTO.toDomain(): NotificationModel {
    return NotificationModel(
        id = id ?: 0,
        title = title ?: "",
        description = description ?: "",
        time = time ?: "",
        icon = Res.drawable.ic_notification,
        isUnread = isUnread ?: true
    )
}