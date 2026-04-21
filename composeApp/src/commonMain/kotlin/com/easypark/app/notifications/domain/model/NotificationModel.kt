package com.easypark.app.notifications.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class NotificationModel(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    val icon: DrawableResource,
    val isUnread: Boolean
)