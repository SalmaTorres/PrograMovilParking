package com.easypark.app.core.domain.model

import com.easypark.app.core.domain.model.status.UserType
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Int,
    val name: String,
    val type: UserType,
    val email: String,
    val password: String,
    val cellphone: Int,
)