package com.easypark.app.shared.domain.model

data class User(
    val id: String,
    val email: String,
    val type: UserType
)