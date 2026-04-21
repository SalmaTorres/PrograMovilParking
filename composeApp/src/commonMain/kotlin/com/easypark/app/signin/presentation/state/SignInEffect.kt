package com.easypark.app.signin.presentation.state

sealed interface SignInEffect {
    data class NavigateToHome(
        val userType: String
    ) : SignInEffect
    object NavigateToRegister : SignInEffect
    data class ShowError(
        val message: String
    ) : SignInEffect
}