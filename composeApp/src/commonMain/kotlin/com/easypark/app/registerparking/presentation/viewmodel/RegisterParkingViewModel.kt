package com.easypark.app.registerparking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.registerparking.domain.usecase.RegisterParkingUseCase
import com.easypark.app.registerparking.presentation.state.*
import com.easypark.app.core.domain.model.Currency
import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.Price
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterParkingViewModel(
    private val registerUseCase: RegisterParkingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterParkingUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterParkingEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RegisterParkingEvent) {
        when (event) {
            is RegisterParkingEvent.OnNameChanged -> _state.update {
                it.copy(name = event.name, isNameError = false)
            }
            is RegisterParkingEvent.OnAddressChanged -> _state.update {
                it.copy(address = event.address, isAddressError = false)
            }
            is RegisterParkingEvent.OnPriceChanged -> _state.update {
                it.copy(pricePerHour = event.price, isPriceError = false)
            }
            is RegisterParkingEvent.OnSpacesChanged -> _state.update {
                it.copy(totalSpaces = event.spaces, isSpacesError = false)
            }
            is RegisterParkingEvent.OnLocationChanged -> {
                _state.update {
                    it.copy(
                        latitude = event.lat,
                        longitude = event.lng
                    )
                }
            }
            RegisterParkingEvent.OnClickRegister -> sendRegistration()
            RegisterParkingEvent.OnClickBack -> emit(RegisterParkingEffect.NavigateBack)
        }
    }

    private fun sendRegistration() {
        val s = _state.value

        // 1. Validación de campos obligatorios
        val hasError = s.name.isEmpty() || s.address.isEmpty() ||
                s.pricePerHour.isEmpty() || s.totalSpaces.isEmpty()

        if (hasError) {
            _state.update { it.copy(
                isNameError = it.name.isEmpty(),
                isAddressError = it.address.isEmpty(),
                isPriceError = it.pricePerHour.isEmpty(),
                isSpacesError = it.totalSpaces.isEmpty()
            )}
            emit(RegisterParkingEffect.ShowError("Completa todos los campos obligatorios"))
            return
        }

        // 2. Ejecución del proceso de registro (Mock)
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val model = ParkingModel(
                id = "owner_123", // ID Mock del dueño actual
                name = s.name,
                address = s.address,
                latitude = s.latitude,
                longitude = s.longitude,
                pricePerHour = Price(
                    amount = s.pricePerHour.toDoubleOrNull() ?: 0.0,
                    currency = Currency.BOB
                ),
                isAvailable = true,
                totalSpaces = s.totalSpaces.toIntOrNull() ?: 0
            )

            // Asumimos que el UseCase devuelve Boolean para mantener consistencia con los anteriores
            val result = registerUseCase.invoke(model)
            _state.update { it.copy(isLoading = false) }

            if (result) {
                emit(RegisterParkingEffect.NavigateToSuccess)
            } else {
                emit(RegisterParkingEffect.ShowError("Error al registrar el parqueo"))
            }
        }
    }

    private fun emit(effect: RegisterParkingEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}