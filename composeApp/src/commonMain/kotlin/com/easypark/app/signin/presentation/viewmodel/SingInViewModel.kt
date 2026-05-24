package com.easypark.app.signin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.signin.domain.usecase.DoLoginUseCase
import com.easypark.app.signin.presentation.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignInViewModel(
    private val useCase: DoLoginUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(SignInUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignInEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email, isEmailError = event.email.isEmpty()) }
            }
            is SignInEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password, isPasswordError = event.password.isEmpty()) }
            }
            SignInEvent.OnLoginClick -> login()
            SignInEvent.OnRegisterClick -> emit(SignInEffect.NavigateToRegister)
        }
    }

    private fun login() {
        val currentState = _state.value
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        val isEmailValid = emailRegex.matches(currentState.email)
        val isPasswordValid = currentState.password.length >= 6

        if (!isEmailValid || !isPasswordValid) {
            _state.update { it.copy(
                isEmailError = !isEmailValid,
                isPasswordError = !isPasswordValid
            )}
            val errorMsg = when {
                !isEmailValid -> "Por favor ingresa un correo electrónico válido."
                else -> "La contraseña debe tener al menos 6 caracteres."
            }
            emit(SignInEffect.ShowError(errorMsg))
            return
        }


        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val userTypeResult = useCase.invoke(currentState.email, currentState.password)
                _state.update { it.copy(isLoading = false) }

                if (userTypeResult != null) {
                    emit(SignInEffect.NavigateToHome(userTypeResult))
                } else {
                    emit(SignInEffect.ShowError("Email o contraseña incorrectos"))
                }
            } catch (e: Exception) {
                io.sentry.kotlin.multiplatform.Sentry.captureException(e)
                _state.update { it.copy(isLoading = false) }
                emit(SignInEffect.ShowError("Ocurrió un error inesperado"))
            }
        }
    }

    private fun emit(effect: SignInEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}