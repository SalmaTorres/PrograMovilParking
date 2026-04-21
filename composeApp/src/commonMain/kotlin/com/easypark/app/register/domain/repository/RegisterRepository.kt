package com.easypark.app.register.domain.repository

interface RegisterRepository {
    suspend fun isEmailAvailable(email: String): Boolean
}