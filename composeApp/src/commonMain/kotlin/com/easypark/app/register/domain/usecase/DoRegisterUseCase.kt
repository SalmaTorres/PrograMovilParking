package com.easypark.app.register.domain.usecase

import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.register.domain.repository.RegisterRepository

class DoRegisterUseCase(private val repository: RegisterRepository) {
    suspend fun checkEmail(email: String): Boolean {
        return repository.isEmailAvailable(email)
    }

    suspend fun saveCloud(user: UserModel) = repository.saveUserToCloud(user)
}