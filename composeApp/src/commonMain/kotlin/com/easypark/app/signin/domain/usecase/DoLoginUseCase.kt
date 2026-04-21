package com.easypark.app.signin.domain.usecase

import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.signin.domain.repository.AuthRepository

class DoLoginUseCase(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(email: String, pass: String): UserType? {
        val user = repository.login(email, pass)

        if (user != null) {
            var parkingId: Int? = null

            if (user.type == UserType.OWNER) {
                parkingId = repository.getParkingIdByOwner(user.id)
            }

            sessionManager.saveSession(user, parkingId)
        }
        return user?.type
    }
}