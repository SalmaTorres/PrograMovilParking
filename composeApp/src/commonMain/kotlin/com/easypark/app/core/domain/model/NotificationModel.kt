package com.easypark.app.core.domain.model

class NotificationModel (
    val id: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val state: String,
)