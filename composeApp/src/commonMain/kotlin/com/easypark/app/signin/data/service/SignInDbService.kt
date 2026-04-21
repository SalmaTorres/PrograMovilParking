package com.easypark.app.signin.data.service

import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.signin.data.dao.SignInDao
import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.signin.data.datasource.SignInLocalDataSource

class SignInDbService (private val signInDao: SignInDao) : SignInLocalDataSource {
    override suspend fun findByEmail(email: String): UserEntity? {
        return signInDao.getByEmail(email)
    }
    override suspend fun getParkingByOwner(ownerId: Int): ParkingEntity? {
        return signInDao.getParkingByOwner(ownerId)
    }
}