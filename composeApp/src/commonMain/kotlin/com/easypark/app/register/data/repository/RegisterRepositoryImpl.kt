package com.easypark.app.register.data.repository

import com.easypark.app.core.data.dto.UserDTO
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.register.data.datasource.RegisterLocalDataSource
import com.easypark.app.register.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RegisterRepositoryImpl(
    private val localDataSource: RegisterLocalDataSource,
    private val firebaseManager: FirebaseManager
) : RegisterRepository {
    override suspend fun isEmailAvailable(email: String): Boolean {
        if (localDataSource.isEmailTaken(email)) return false
        return try {
            val sanitizedEmail = email.replace(".", "_")
            val jsonUser = firebaseManager.observeData("users/$sanitizedEmail").firstOrNull()
            jsonUser == null || jsonUser == "null"
        } catch (e: Exception) {
            true
        }
    }

    override suspend fun saveUserToCloud(user: UserModel) {
        val userDto = UserDTO(
            id = user.id,
            name = user.name,
            email = user.email,
            cellphone = user.cellphone,
            type = user.type.name
        )
        val json = Json.encodeToString(userDto)
        val sanitizedEmail = user.email.replace(".", "_")
        firebaseManager.saveData("users/$sanitizedEmail", json)
    }
}