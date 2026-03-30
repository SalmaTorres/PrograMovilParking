import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.registerparking.domain.usecase.RegisterParkingUseCase
import com.easypark.app.registerparking.presentation.state.RegisterParkingEffect
import com.easypark.app.registerparking.presentation.state.RegisterParkingEvent
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// registerparking/presentation/viewmodel/RegisterParkingViewModel.kt
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
                it.copy(name = event.value, isNameError = event.value.isEmpty())
            }
            is RegisterParkingEvent.OnAddressChanged -> _state.update {
                it.copy(address = event.value, isAddressError = event.value.isEmpty())
            }
            is RegisterParkingEvent.OnPriceChanged -> _state.update {
                it.copy(pricePerHour = event.value, isPriceError = event.value.isEmpty())
            }
            is RegisterParkingEvent.OnSpacesChanged -> _state.update {
                it.copy(totalSpaces = event.value, isSpacesError = event.value.isEmpty())
            }
            is RegisterParkingEvent.OnLocationChanged -> _state.update {
                it.copy(latitude = event.lat, longitude = event.lng)
            }
            RegisterParkingEvent.OnClickRegister -> sendRegistration()
            RegisterParkingEvent.OnClickBack -> emit(RegisterParkingEffect.NavigateBack)
        }
    }

    private fun sendRegistration() {
        val currentState = _state.value

        // 1. Validar campos vacíos y prender los errores en el State
        val hasError = currentState.name.isEmpty() ||
                currentState.address.isEmpty() ||
                currentState.pricePerHour.isEmpty() ||
                currentState.totalSpaces.isEmpty()

        if (hasError) {
            _state.update { it.copy(
                isNameError = it.name.isEmpty(),
                isAddressError = it.address.isEmpty(),
                isPriceError = it.pricePerHour.isEmpty(),
                isSpacesError = it.totalSpaces.isEmpty()
            )}
            emit(RegisterParkingEffect.ShowToast("Por favor completa los campos marcados"))
            return
        }

        // 2. Si no hay errores, procedemos al registro
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val model = ParkingModel(
                ownerId = "user_mock_123",
                name = currentState.name,
                address = currentState.address,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                pricePerHour = currentState.pricePerHour.toDoubleOrNull() ?: 0.0,
                totalSpaces = currentState.totalSpaces.toIntOrNull() ?: 0 // Ya no fallará
            )

            val result = registerUseCase.invoke(model)
            _state.update { it.copy(isLoading = false) }

            if (result.isSuccess) { // Ahora 'isSuccess' existe porque UseCase devuelve Result
                emit(RegisterParkingEffect.NavigateToSuccess)
            } else {
                emit(RegisterParkingEffect.ShowToast("Error en el servidor mock"))
            }
        }
    }

    private fun emit(effect: RegisterParkingEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}