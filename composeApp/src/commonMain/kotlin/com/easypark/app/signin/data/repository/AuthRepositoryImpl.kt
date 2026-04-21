package com.easypark.app.signin.data.repository

import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.signin.data.datasource.SignInLocalDataSource
import com.easypark.app.signin.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val localDataSource: SignInLocalDataSource
) : AuthRepository {

    override suspend fun login(email: String, pass: String): UserModel? {
        val entity = localDataSource.findByEmail(email)

        return if (entity != null && entity.password == pass) {
            entity.toModel()
        } else {
            null
        }
    }
    override suspend fun getParkingIdByOwner(ownerId: Int): Int? {
        return localDataSource.getParkingByOwner(ownerId)?.id
    }
}