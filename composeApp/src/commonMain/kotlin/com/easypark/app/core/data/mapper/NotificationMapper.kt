package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.core.domain.model.NotificationModel

fun NotificationModel.toEntity() = NotificationEntity(
    userId,
    title,
    content,
    state
)

fun NotificationEntity.toModel() =  NotificationModel(
    id,
    userId,
    title,
    content,
    state
)