package com.easypark.app.signin.domain.repository

interface AuthenticationRepository {

    suspend fun login(email: String, password: String): Boolean

}