package com.easypark.app.registervehicle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.registervehicle.domain.model.VehicleModel // Importamos el nuevo modelo
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
        val user = userFromStep1
        if (user == null) {
            println("RegisterVehicleViewModel: [submit] ABORTED: userFromStep1 is null!")
            return
        }

        println("RegisterVehicleViewModel: [submit] Initiating vehicle registration. User: ${user.email}, Plate: ${s.plate}, Type: ${s.type}")

        // Validación básica
        val hasError = s.plate.isEmpty() || s.plate.length > 7
        if (hasError) {
            println("RegisterVehicleViewModel: [submit] Validation FAILED. plateEmpty: ${s.plate.isEmpty()}, plateLength: ${s.plate.length}")
            _state.update { it.copy(isPlateError = hasError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. CREAMOS EL MODELO DEL VEHÍCULO
            val finalVehicle = VehicleModel(
                id = 0,
                driverId = 0,
                plate = s.plate,
                type = s.type.displayName,
                model = "",
                color = ""
            )

            println("RegisterVehicleViewModel: [submit] Calling RegisterVehicleUseCase...")
            val registeredUserId = useCase(user, finalVehicle)
            println("RegisterVehicleViewModel: [submit] RegisterVehicleUseCase returned: registeredUserId = $registeredUserId")

            _state.update { it.copy(isLoading = false) }

            if (registeredUserId != null) {
                // Actualizamos el ID del usuario localmente para la sesión
                val userToSave = user.copy(id = registeredUserId)

                println("RegisterVehicleViewModel: [submit] Saving session for driver ID: $registeredUserId")
                sessionManager.saveSession(userToSave, null)
                println("RegisterVehicleViewModel: [submit] Session saved. Directing to next screen.")

                emit(RegisterVehicleEffect.NavigateNext)
            } else {
                println("RegisterVehicleViewModel: [submit] Vehicle registration failed in repository/datasource.")
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