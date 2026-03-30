package com.easypark.app.signin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.signin.domain.usecase.DoLoginUseCase
import com.easypark.app.signin.presentation.state.*
import kotlinx.coroutines.channels.Channel
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
        if (currentState.email.isEmpty() || currentState.password.isEmpty()) {
            _state.update { it.copy(
                isEmailError = it.email.isEmpty(),
                isPasswordError = it.password.isEmpty()
            )}
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = useCase.invoke(currentState.email, currentState.password)
            _state.update { it.copy(isLoading = false) }

            if (result) {
                emit(SignInEffect.NavigateToHome)
            } else {
                emit(SignInEffect.ShowError("Credenciales incorrectas"))
            }
        }
    }

    private fun emit(effect: SignInEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}