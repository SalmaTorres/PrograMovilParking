package com.easypark.app.notifications.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO (
    val id: Int? = 0,
    val title: String? = "",
    val description: String? = "",
    val time: String? = "",
    val isUnread: Boolean? = true
)