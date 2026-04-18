package com.easypark.app.core.data.service

import com.easypark.app.core.data.dao.UserDao
import com.easypark.app.core.data.datasource.UserLocalDataSource
import com.easypark.app.core.data.entity.UserEntity

class UserDbService(val userDao: UserDao) : UserLocalDataSource {
    override suspend fun save(entity: UserEntity): Int {
        return userDao.insert(entity).toInt()
    }

    override suspend fun findByEmail(email: String): UserEntity? {
        return userDao.getByEmail(email)
    }

    override suspend fun isEmailTaken(email: String): Boolean {
        return userDao.countEmail(email) > 0
    }
}