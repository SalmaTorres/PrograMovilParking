package com.easypark.app.core.domain.session

import com.easypark.app.core.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class SessionManager {
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    open val currentUser = _currentUser.asStateFlow()
    open var currentParkingId: Int? = null

    open fun saveSession(user: UserModel, parkingId: Int? = null) {
        _currentUser.value = user
        this.currentParkingId = parkingId
    }

    open fun getUserId(): Int = _currentUser.value?.id ?: -1

    open fun clearSession() {
        _currentUser.value = null
        currentParkingId = null
    }
}