package com.easypark.app.register.data.datasource

import com.easypark.app.core.data.entity.UserEntity

interface RegisterLocalDataSource {
    suspend fun isEmailTaken(email: String): Boolean
}