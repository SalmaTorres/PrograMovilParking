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
        val user = userFromStep1 ?: return

        // Validación básica
        val hasError = s.plate.isEmpty() || s.plate.length > 7
        if (hasError) {
            _state.update { it.copy(isPlateError = hasError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. CREAMOS EL MODELO DEL VEHÍCULO
            // Nota: driverId es 0 porque el Repositorio lo asignará
            // cuando genere el ID del usuario en Room/Firebase.
            val finalVehicle = VehicleModel(
                id = 0,
                driverId = 0,
                plate = s.plate,
                type = s.type.displayName,
                model = "", // Puedes añadir estos campos al UIState luego si quieres
                color = ""
            )

            // 2. ENVIAMOS AMBOS AL USE CASE (Soluciona tu error de la imagen)
            val registeredUserId = useCase(user, finalVehicle)

            _state.update { it.copy(isLoading = false) }

            if (registeredUserId != null) {
                // Actualizamos el ID del usuario localmente para la sesión
                val userToSave = user.copy(id = registeredUserId)

                // Guardamos la sesión y el vehículo en el SessionManager
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