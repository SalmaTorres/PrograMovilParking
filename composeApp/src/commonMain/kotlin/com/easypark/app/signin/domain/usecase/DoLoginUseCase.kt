package com.easypark.app.signin.domain.usecase

import com.easypark.app.core.domain.model.UserType
import com.easypark.app.core.domain.repository.AuthRepository

class DoLoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): UserType? {
        val user = repository.login(email, password)

        return user?.type
    }
}