package com.easypark.app.signin.domain.usecase

import com.easypark.app.core.domain.model.UserType
import com.easypark.app.signin.domain.repository.AuthenticationRepository

class DoLoginUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): UserType? {
        return repository.login(email, password)
    }
}