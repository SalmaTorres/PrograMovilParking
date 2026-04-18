package com.easypark.app.core.data.repository

import com.easypark.app.core.data.datasource.UserLocalDataSource
import com.easypark.app.core.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val localDataSource: UserLocalDataSource
) : RegisterRepository {
    override suspend fun isEmailAvailable(email: String): Boolean {
        return !localDataSource.isEmailTaken(email)
    }
}