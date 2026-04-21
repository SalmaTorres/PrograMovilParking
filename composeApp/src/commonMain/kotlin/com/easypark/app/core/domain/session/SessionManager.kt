package com.easypark.app.core.domain.session

import com.easypark.app.core.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()
    var currentParkingId: Int? = null

    fun saveSession(user: UserModel, parkingId: Int? = null) {
        _currentUser.value = user
        this.currentParkingId = parkingId
    }

    fun getUserId(): Int = _currentUser.value?.id ?: -1

    fun clearSession() {
        _currentUser.value = null
        currentParkingId = null
    }
}