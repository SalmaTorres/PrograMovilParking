package com.easypark.app.core.data.dto

import com.easypark.app.core.domain.model.status.UserType
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO (
    val id: Int? = 0,
    val name: String? = "",
    val email: String? = "",
    val cellphone: Int? = 0,
    val type: String? = UserType.DRIVER.name
)