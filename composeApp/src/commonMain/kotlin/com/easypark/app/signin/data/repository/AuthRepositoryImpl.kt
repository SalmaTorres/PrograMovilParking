package com.easypark.app.signin.data.repository

import com.easypark.app.core.data.dto.UserDTO
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.signin.data.datasource.SignInLocalDataSource
import com.easypark.app.signin.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val localDataSource: SignInLocalDataSource,
    private val firebaseManager: FirebaseManager
) : AuthRepository {

    override suspend fun login(email: String, pass: String): UserModel? {
        val entity = localDataSource.findByEmail(email)

        return if (entity != null && entity.password == pass) {
            entity.toModel()
        } else {
            loginFromCloud(email, pass)
        }
    }

    override suspend fun loginFromCloud(email: String, pass: String): UserModel? {
        val sanitizedEmail = email.replace(".", "_")

        val jsonUser = firebaseManager.observeData("users/$sanitizedEmail").firstOrNull()
            ?: return null

        val dto = Json.decodeFromString<UserDTO>(jsonUser)

        return if (dto.email == email) {
            dto.toDomain()
        } else {
            null
        }
    }

    override suspend fun getParkingIdByOwner(ownerId: Int): Int? {
        return localDataSource.getParkingByOwner(ownerId)?.id
    }
}