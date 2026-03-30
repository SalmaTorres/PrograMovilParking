package com.easypark.app.register.data.repository

import com.easypark.app.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl : RegisterRepository {

    override suspend fun register(
        name: String, email: String, phone: String, password: String, role: String
    ): Boolean {
        // Simulamos validaciones de "Base de Datos" (Mock)
        if (name.length < 3) return false
        if (!email.contains("@")) return false
        if (phone.length < 7) return false
        if (password.length < 6) return false

        return true
    }
}