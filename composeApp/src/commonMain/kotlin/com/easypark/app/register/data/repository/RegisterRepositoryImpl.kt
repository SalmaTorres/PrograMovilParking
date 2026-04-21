package com.easypark.app.register.data.repository

import com.easypark.app.register.data.datasource.RegisterLocalDataSource
import com.easypark.app.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val localDataSource: RegisterLocalDataSource
) : RegisterRepository {
    override suspend fun isEmailAvailable(email: String): Boolean {
        return !localDataSource.isEmailTaken(email)
    }
}