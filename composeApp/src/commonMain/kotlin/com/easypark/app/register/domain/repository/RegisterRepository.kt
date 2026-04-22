package com.easypark.app.register.domain.repository

import com.easypark.app.core.domain.model.UserModel

interface RegisterRepository {
    suspend fun isEmailAvailable(email: String): Boolean
    suspend fun saveUserToCloud(user: UserModel)
}