package com.easypark.app.signin.domain.usecase

import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.signin.domain.repository.AuthRepository

class DoLoginUseCase(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) {
    suspend fun invoke(email: String, pass: String): String? {
        val user = repository.login(email, pass) ?: return null

        var parkingId: Int? = null
        if (user.type.name == "OWNER") {
            parkingId = repository.getParkingIdByOwner(user.id)
        }

        sessionManager.saveSession(user, parkingId)
        return user.type.name
    }
}