package com.easypark.app.registervehicle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.usecase.RegisterVehicleUseCase
import com.easypark.app.registervehicle.presentation.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterVehicleViewModel(
    private val useCase: RegisterVehicleUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var userFromStep1: UserModel? = null
    private val _state = MutableStateFlow(RegisterVehicleUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterVehicleEffect>()
    val effect = _effect.asSharedFlow()

    fun initUser(user: UserModel) {
        this.userFromStep1 = user
    }

    fun onEvent(event: RegisterVehicleEvent) {
        when (event) {

            is RegisterVehicleEvent.OnPlateChange -> {
                if (event.plate.length <= 7) {
                    _state.update {
                        it.copy(plate = event.plate.uppercase(), isPlateError = false)
                    }
                }
            }

            is RegisterVehicleEvent.OnTypeChange -> _state.update {
                it.copy(type = event.type)
            }

            RegisterVehicleEvent.OnSubmitClick -> submit()
            RegisterVehicleEvent.OnBackClick -> emit(RegisterVehicleEffect.NavigateBack)
        }
    }

    private fun submit() {
        val s = _state.value
        val user = userFromStep1 ?: return

        val hasError = s.plate.isEmpty() || s.plate.length > 7

        if (hasError) {
            _state.update {
                it.copy(
                    isPlateError = hasError
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val user = userFromStep1 ?: return@launch
            val finalUser = user.copy(
                placaVehiculo = s.plate,
                tipoVehiculo = s.type.displayName
            )

            val registeredUserId = useCase(finalUser)

            _state.update { it.copy(isLoading = false) }

            if (registeredUserId != null) {
                val userToSave = finalUser.copy(id = registeredUserId)

                sessionManager.saveSession(userToSave, null)

                emit(RegisterVehicleEffect.NavigateNext)
            } else {
                emit(RegisterVehicleEffect.ShowError("Error al registrar el vehículo"))
            }
        }
    }

    private fun emit(effect: RegisterVehicleEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}