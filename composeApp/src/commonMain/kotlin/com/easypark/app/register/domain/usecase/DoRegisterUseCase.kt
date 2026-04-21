package com.easypark.app.register.domain.usecase

import com.easypark.app.register.domain.repository.RegisterRepository

class DoRegisterUseCase(private val repository: RegisterRepository) {
    suspend operator fun invoke(email: String): Boolean {
        return repository.isEmailAvailable(email)
    }
}