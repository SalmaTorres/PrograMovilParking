package com.easypark.app.registervehicle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.registervehicle.domain.model.RegisterVehicleModel
import com.easypark.app.registervehicle.domain.usecase.RegisterVehicleUseCase
import com.easypark.app.registervehicle.presentation.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterVehicleViewModel(
    private val useCase: RegisterVehicleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterVehicleUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterVehicleEffect>()
    val effect = _effect.asSharedFlow()

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

        val hasError = s.plate.isEmpty() || s.model.isEmpty() || s.color.isEmpty()

        if (hasError) {
            _state.update {
                it.copy(
                    isPlateError = it.plate.isEmpty(),
                    isModelError = it.model.isEmpty(),
                    isColorError = it.color.isEmpty()
                )
            }
            emit(RegisterVehicleEffect.ShowError("Completa todos los campos"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val model = RegisterVehicleModel(
                plate = s.plate,
                model = s.model,
                color = s.color
            )

            val result = useCase(model)

            _state.update { it.copy(isLoading = false) }

            if (result) {
                emit(RegisterVehicleEffect.NavigateNext)
            } else {
                emit(RegisterVehicleEffect.ShowError("Error al registrar"))
            }
        }
    }

    private fun emit(effect: RegisterVehicleEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}