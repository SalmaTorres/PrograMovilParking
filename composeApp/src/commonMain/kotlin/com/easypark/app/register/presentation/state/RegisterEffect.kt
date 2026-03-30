package com.easypark.app.register.presentation.state

sealed interface RegisterEffect {
    object NavigateToLogin : RegisterEffect
    object NavigateToRegisterVehicle : RegisterEffect
    object NavigateToRegisterParking : RegisterEffect
    data class ShowError(val message: String) : RegisterEffect
}