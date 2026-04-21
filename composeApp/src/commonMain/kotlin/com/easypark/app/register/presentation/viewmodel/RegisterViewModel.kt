package com.easypark.app.register.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.register.presentation.state.*
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
            is RegisterEvent.OnNameChange -> _state.update {
                it.copy(name = event.name, isNameError = false)
            }
            is RegisterEvent.OnEmailChange -> _state.update {
                it.copy(email = event.email, isEmailError = false)
            }
            is RegisterEvent.OnPhoneChange -> _state.update {
                it.copy(phone = event.phone, isPhoneError = false)
            }
            is RegisterEvent.OnPasswordChange -> _state.update {
                it.copy(password = event.password, isPasswordError = false)
            }
            is RegisterEvent.OnRoleSelected -> {
                _state.update { it.copy(role = event.role) }
            }
            RegisterEvent.OnRegisterClick -> register()
            RegisterEvent.OnLoginClick -> emit(RegisterEffect.NavigateToLogin)
        }
    }

    private fun register() {
        val s = _state.value

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val isEmailValid = s.email.matches(emailPattern.toRegex())
        val isPasswordValid = s.password.length >= 6

        val hasError = s.name.isEmpty() || !isEmailValid || s.phone.isEmpty() || !isPasswordValid

        if (hasError) {
            _state.update { it.copy(
                isNameError = it.name.isEmpty(),
                isEmailError = !isEmailValid,
                isPhoneError = it.phone.isEmpty(),
                isPasswordError = !isPasswordValid
            )}

            val errorMsg = if(!isEmailValid) "Email inválido" else "La contraseña debe tener 6+ caracteres"
            emit(RegisterEffect.ShowError(errorMsg))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val isAvailable = useCase.invoke(s.email)
            _state.update { it.copy(isLoading = false) }

            if (isAvailable) {
                val userData = UserModel(
                    id = 0,
                    name = s.name,
                    email = s.email,
                    cellphone = s.phone.toIntOrNull() ?: 0,
                    password = s.password,
                    type = s.role
                )

                if (s.role == UserType.DRIVER) {
                    emit(RegisterEffect.NavigateToRegisterVehicle(userData))
                } else {
                    emit(RegisterEffect.NavigateToRegisterParking(userData))
                }
            } else {
                emit(RegisterEffect.ShowError("Email ya registrado"))
            }
        }
    }

    private fun emit(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

}