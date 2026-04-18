package com.easypark.app.register.presentation.state

import com.easypark.app.core.domain.model.UserModel

sealed interface RegisterEffect {
    object NavigateToLogin : RegisterEffect
    data class NavigateToRegisterVehicle(val user: UserModel) : RegisterEffect
    data class NavigateToRegisterParking(val user: UserModel) : RegisterEffect
    data class ShowError(val message: String) : RegisterEffect
}