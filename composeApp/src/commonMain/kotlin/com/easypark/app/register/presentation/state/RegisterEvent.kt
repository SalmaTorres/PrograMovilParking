package com.easypark.app.register.presentation.state

import com.easypark.app.shared.domain.model.UserType

sealed interface RegisterEvent {

    data class OnNameChange(val name: String) : RegisterEvent
    data class OnEmailChange(val email: String) : RegisterEvent
    data class OnPhoneChange(val phone: String) : RegisterEvent
    data class OnPasswordChange(val password: String) : RegisterEvent

    data class OnRoleSelected(val role: UserType) : RegisterEvent

    data object OnRegisterClick : RegisterEvent
    data object OnLoginClick : RegisterEvent
}