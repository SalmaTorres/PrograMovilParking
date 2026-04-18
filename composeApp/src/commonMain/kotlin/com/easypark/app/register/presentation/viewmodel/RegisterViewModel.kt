package com.easypark.app.register.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.register.presentation.state.*
import com.easypark.app.core.domain.model.UserType
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
            // Al escribir, quitamos el estado de error inmediatamente
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
         val hasError = s.name.isEmpty() || s.email.isEmpty() || s.phone.isEmpty() || s.password.isEmpty()

        if (hasError) {
            _state.update { it.copy(
                isNameError = it.name.isEmpty(),
                isEmailError = it.email.isEmpty(),
                isPhoneError = it.phone.isEmpty(),
                isPasswordError = it.password.isEmpty()
            )}
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
                emit(RegisterEffect.ShowError("El correo electrónico ya está registrado"))
            }
        }
    }

    private fun emit(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

}