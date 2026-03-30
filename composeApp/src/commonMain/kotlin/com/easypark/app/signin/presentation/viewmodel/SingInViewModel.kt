package com.easypark.app.signin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.signin.presentation.state.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInUIState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SignInEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SignInEvent) {
        when (event) {

            is SignInEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }

            is SignInEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password) }
            }

            SignInEvent.OnLoginClick -> login()

            SignInEvent.OnRegisterClick -> {
                viewModelScope.launch {
                    _effect.send(SignInEffect.NavigateToRegister)
                }
            }
        }
    }

    private fun login() {
        val current = _state.value

        val isValid = current.email.isNotBlank() && current.password.length >= 6

        viewModelScope.launch {
            if (isValid) {
                _effect.send(SignInEffect.NavigateToHome)
            } else {
                _effect.send(SignInEffect.ShowError("Datos inválidos"))
            }
        }
    }
}