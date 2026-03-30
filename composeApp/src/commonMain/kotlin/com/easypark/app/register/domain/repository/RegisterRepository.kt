package com.easypark.app.register.domain.repository

import com.easypark.app.register.domain.model.RegisterModel

interface RegisterRepository {

    suspend fun register(name: String, email: String, phone: String, password: String, role: String): Boolean

}