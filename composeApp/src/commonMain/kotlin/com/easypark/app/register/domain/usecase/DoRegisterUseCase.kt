package com.easypark.app.register.domain.usecase

import com.easypark.app.register.domain.model.RegisterModel
import com.easypark.app.register.domain.repository.RegisterRepository

class DoRegisterUseCase(
    private val repository: RegisterRepository
) {

    suspend operator fun invoke(name: String, email: String, phone: String, password: String, role: String): Boolean {
        return repository.register(name, email, phone, password, role)
    }
}