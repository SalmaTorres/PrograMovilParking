package com.easypark.app.register.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.register.domain.model.RegisterModel
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.register.presentation.state.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val useCase: DoRegisterUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(RegisterUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {

            is RegisterEvent.OnNameChange -> {
                _state.update { it.copy(name = event.name) }
            }

            is RegisterEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }

            is RegisterEvent.OnPhoneChange -> {
                _state.update { it.copy(phone = event.phone) }
            }

            is RegisterEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password) }
            }

            is RegisterEvent.OnRoleSelected -> {
                _state.update { it.copy(role = event.role) }
            }

            RegisterEvent.OnRegisterClick -> register()

            RegisterEvent.OnLoginClick -> emit(RegisterEffect.NavigateToLogin)
        }
    }

    private fun register() {
        val current = _state.value
        if (current.name.isEmpty() || current.email.isEmpty() || current.password.isEmpty() || current.phone.isEmpty()) {
            _state.update { it.copy(
                isEmailError = it.email.isEmpty(),
                isPasswordError = it.password.isEmpty(),
                isPhoneError = it.phone.isEmpty(),
                isNameError = it.name.isEmpty()
            )}
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = useCase.invoke(current.name, current.email, current.phone, current.password, current.role)
            _state.update { it.copy(isLoading = false) }

            if (result) {
                emit(RegisterEffect.NavigateToNext)
            } else {
                emit(RegisterEffect.ShowError("Error al registrar usuario"))
            }
        }
    }

    private fun emit(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

}