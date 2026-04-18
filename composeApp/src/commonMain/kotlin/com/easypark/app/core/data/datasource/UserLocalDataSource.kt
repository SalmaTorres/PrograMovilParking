package com.easypark.app.core.data.datasource

import com.easypark.app.core.data.entity.UserEntity

interface UserLocalDataSource {
    suspend fun save(entity: UserEntity): Int
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun isEmailTaken(email: String): Boolean
}