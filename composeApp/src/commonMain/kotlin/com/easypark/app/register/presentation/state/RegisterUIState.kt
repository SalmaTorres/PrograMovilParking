package com.easypark.app.register.presentation.state

data class RegisterUIState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val role: String = "driver",
    val isLoading: Boolean = false,
    val isNameError: Boolean = false,
    val isEmailError: Boolean = false,
    val isPhoneError: Boolean = false,
    val isPasswordError: Boolean = false,

)