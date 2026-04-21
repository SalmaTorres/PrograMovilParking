package com.easypark.app.registerparking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.registerparking.domain.usecase.RegisterParkingUseCase
import com.easypark.app.registerparking.presentation.state.*
import com.easypark.app.core.domain.model.status.Currency
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.session.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterParkingViewModel(
    private val registerUseCase: RegisterParkingUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private var userFromStep1: UserModel? = null

    private val _state = MutableStateFlow(RegisterParkingUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterParkingEffect>()
    val effect = _effect.asSharedFlow()

    fun initUser(user: UserModel) {
        this.userFromStep1 = user
    }

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
        val user = userFromStep1 ?: return

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

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val parkingModel = ParkingModel(
                id = 0,
                ownerId = 0,
                name = s.name,
                address = s.address,
                latitude = s.latitude,
                longitude = s.longitude,
                pricePerHour = PriceModel(
                    amount = s.pricePerHour.toDoubleOrNull() ?: 0.0,
                    currency = Currency.BOB
                ),
                rating = 0.0f,
                totalSpaces = s.totalSpaces.toIntOrNull() ?: 0
            )

            val resultIds = registerUseCase(user, parkingModel)

            _state.update { it.copy(isLoading = false) }

            if (resultIds != null) {
                val registeredUser = user.copy(id = resultIds.first)

                sessionManager.saveSession(registeredUser, resultIds.second)

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