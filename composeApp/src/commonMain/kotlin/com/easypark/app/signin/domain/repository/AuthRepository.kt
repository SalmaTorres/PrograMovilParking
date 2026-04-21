package com.easypark.app.signin.domain.repository

import com.easypark.app.core.domain.model.UserModel

interface AuthRepository {
    suspend fun login(email: String, pass: String): UserModel?
    suspend fun getParkingIdByOwner(ownerId: Int): Int?
}