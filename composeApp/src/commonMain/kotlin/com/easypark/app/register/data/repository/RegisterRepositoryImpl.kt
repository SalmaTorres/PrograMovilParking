package com.easypark.app.register.data.repository

import com.easypark.app.core.data.dto.UserDTO
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.register.data.datasource.RegisterLocalDataSource
import com.easypark.app.register.domain.repository.RegisterRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RegisterRepositoryImpl(
    private val localDataSource: RegisterLocalDataSource,
    private val firebaseManager: FirebaseManager
) : RegisterRepository {
    override suspend fun isEmailAvailable(email: String): Boolean {
        return !localDataSource.isEmailTaken(email)
    }

    override suspend fun saveUserToCloud(user: UserModel) {
        val userDto = UserDTO(
            id = user.id,
            name = user.name,
            email = user.email,
            cellphone = user.cellphone,
            type = user.type.name
        )

        val jsonUser = Json.encodeToString(userDto)

        val path = if (user.id == 0) "users/${user.email.replace(".", "_")}" else "users/${user.id}"

        firebaseManager.saveData(path, jsonUser)
    }
}