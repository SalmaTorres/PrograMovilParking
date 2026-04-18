package com.easypark.app.core.data.repository

import com.easypark.app.core.data.datasource.UserLocalDataSource
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val localDataSource: UserLocalDataSource
) : AuthRepository {

    override suspend fun login(email: String, pass: String): UserModel? {
        val entity = localDataSource.findByEmail(email)

        return if (entity != null && entity.password == pass) {
            entity.toModel()
        } else {
            null
        }
    }
}