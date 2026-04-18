package com.easypark.app.core.domain.model

data class UserModel(
    val id: Int,
    val name: String,
    val type: UserType,
    val email: String,
    val cellphone: Int,
    val password: String
)