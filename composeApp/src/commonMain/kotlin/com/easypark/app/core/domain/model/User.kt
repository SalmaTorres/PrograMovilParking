package com.easypark.app.core.domain.model

data class User(
    val id: String,
    val email: String,
    val type: UserType
)