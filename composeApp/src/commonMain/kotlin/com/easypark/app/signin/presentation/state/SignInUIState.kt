package com.easypark.app.signin.presentation.state

data class SignInUIState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)