package com.easypark.app.signin.presentation.state

sealed interface SignInEvent {
    data class OnEmailChange(
        val email: String
    ) : SignInEvent
    data class OnPasswordChange(
        val password: String
    ) : SignInEvent
    data object OnLoginClick : SignInEvent
    data object OnRegisterClick : SignInEvent
}