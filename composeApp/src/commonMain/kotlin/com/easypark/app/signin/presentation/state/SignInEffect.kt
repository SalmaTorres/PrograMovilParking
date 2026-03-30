package com.easypark.app.signin.presentation.state

import com.easypark.app.shared.domain.model.UserType

sealed interface SignInEffect {
    data class NavigateToHome(
        val userType: UserType
    ) : SignInEffect
    object NavigateToRegister : SignInEffect
    data class ShowError(
        val message: String
    ) : SignInEffect
}