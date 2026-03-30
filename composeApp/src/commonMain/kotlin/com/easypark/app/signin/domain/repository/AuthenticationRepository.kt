package com.easypark.app.signin.domain.repository

import com.easypark.app.shared.domain.model.User
import com.easypark.app.shared.domain.model.UserType

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): UserType?
    suspend fun register(email: String, pass: String, type: UserType): User?

}