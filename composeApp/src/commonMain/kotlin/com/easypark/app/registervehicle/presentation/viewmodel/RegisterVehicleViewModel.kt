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

            is RegisterVehicleEvent.OnPlateChange -> _state.update {
                it.copy(plate = event.plate, isPlateError = false)
            }

            is RegisterVehicleEvent.OnModelChange -> _state.update {
                it.copy(model = event.model, isModelError = false)
            }

            is RegisterVehicleEvent.OnColorChange -> _state.update {
                it.copy(color = event.color, isColorError = false)
            }

            RegisterVehicleEvent.OnSubmitClick -> submit()
            RegisterVehicleEvent.OnBackClick -> emit(RegisterVehicleEffect.NavigateBack)
        }
    }

    private fun submit() {
        val s = _state.value
        val user = userFromStep1 ?: return

        val hasError = s.plate.isEmpty() || s.model.isEmpty() || s.color.isEmpty()

        if (hasError) {
            _state.update {
                it.copy(
                    isPlateError = it.plate.isEmpty(),
                    isModelError = it.model.isEmpty(),
                    isColorError = it.color.isEmpty()
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val vehicleModel = VehicleModel(
                id = 0,
                driverId = 0,
                plate = s.plate,
                model = s.model,
                color = s.color
            )

            val user = userFromStep1 ?: return@launch

            val registeredUserId = useCase(user, vehicleModel)

            _state.update { it.copy(isLoading = false) }

            if (registeredUserId != null) {
                val finalUser = user.copy(id = registeredUserId)

                sessionManager.saveSession(finalUser, null)

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