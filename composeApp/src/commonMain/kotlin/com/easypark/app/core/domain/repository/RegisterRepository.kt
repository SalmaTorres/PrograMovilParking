package com.easypark.app.core.domain.repository

import com.easypark.app.core.domain.model.UserModel

interface RegisterRepository {
    suspend fun isEmailAvailable(email: String): Boolean
}