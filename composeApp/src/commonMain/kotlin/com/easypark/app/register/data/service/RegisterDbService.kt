package com.easypark.app.register.data.service

import com.easypark.app.register.data.dao.RegisterDao
import com.easypark.app.register.data.datasource.RegisterLocalDataSource

class RegisterDbService (private val registerDao: RegisterDao) : RegisterLocalDataSource {
    override suspend fun isEmailTaken(email: String): Boolean {
        return registerDao.countEmail(email) > 0
    }
}