package com.easypark.app.signin.data.repository

import com.easypark.app.shared.domain.model.User
import com.easypark.app.shared.domain.model.UserType
import com.easypark.app.signin.domain.repository.AuthenticationRepository

class AuthenticationRepositoryImpl : AuthenticationRepository {
    override suspend fun login(email: String, password: String): UserType? {
        return when {
            email == "owner@test.com" && password == "123456" ->
                UserType.OWNER
            email == "driver@test.com" && password == "123456" ->
                UserType.DRIVER
            else -> null
        }
    }

    override suspend fun register(email: String, pass: String, type: UserType): User? {
        return User("new_id", email, type)
    }
}