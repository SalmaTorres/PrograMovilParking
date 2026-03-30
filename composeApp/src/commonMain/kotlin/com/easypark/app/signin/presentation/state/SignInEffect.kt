package com.easypark.app.signin.presentation.state

sealed interface SignInEffect {
    data object NavigateToHome : SignInEffect
    data object NavigateToRegister : SignInEffect
    data class ShowError(val message: String) : SignInEffect
}