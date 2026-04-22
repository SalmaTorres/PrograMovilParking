package com.easypark.app.notifications.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class NotificationDTO (
    val id: Int? = 0,
    val title: String? = "",
    @SerialName("message") val description: String? = "",
    val time: String? = "",
    val isUnread: Boolean? = true
)