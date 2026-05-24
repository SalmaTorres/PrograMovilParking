package com.easypark.app.bookingconfirmation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.bookingconfirmation.domain.usecase.ConfirmReservationUseCase
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingInfoUseCase
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEffect
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationUIState
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingConfirmationViewModel(
    private val parkingId: Int,
    private val getBookingInfoUseCase: GetBookingInfoUseCase,
    private val confirmReservationUseCase: ConfirmReservationUseCase,
    private val repository: BookingConfirmationRepository,
    private val vehicleRepository: RegisterVehicleRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(BookingConfirmationUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BookingConfirmationEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val info = getBookingInfoUseCase(parkingId)
            _state.update { it.copy(isLoading = false, bookingConfirmation = info) }
        }
    }

    fun onEvent(event: BookingConfirmationEvent) {
        when (event) {
            is BookingConfirmationEvent.OnPaymentMethodSelected -> {
                _state.update { it.copy(selectedPaymentMethod = event.method) }
            }
            is BookingConfirmationEvent.OnDurationChange -> {
                _state.update { s ->
                    val newBooking = s.bookingConfirmation?.copy(
                        durationHours = event.hours,
                        totalCost = s.bookingConfirmation.pricePerHour * event.hours
                    )
                    s.copy(bookingConfirmation = newBooking)
                }
            }

            BookingConfirmationEvent.OnBackClick -> emit(BookingConfirmationEffect.NavigateBack)
            is BookingConfirmationEvent.OnConfirmClick -> confirm()
        }
    }

    private fun observeLiveStatus(reservationId: Int) {
        viewModelScope.launch {
            repository.observeBookingRealtime(reservationId.toString()).collect { reservation ->
                reservation?.let { liveData ->
                    _state.update { it.copy(
                        // Suponiendo que tu UIState tenga estos campos
                        // status = liveData.status
                    )}
                    println("LOG TIEMPO REAL: El estado de la reserva ahora es: ${liveData.status}")
                }
            }
        }
    }

    private fun confirm() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentUserId = sessionManager.getUserId()

            // 1. OBTENER EL VEHÍCULO DESDE EL NUEVO REPOSITORIO
            val vehicle = vehicleRepository.getVehicleByDriverId(currentUserId)

            if (vehicle == null) {
                _state.update { it.copy(isLoading = false) }
                emit(BookingConfirmationEffect.ShowError("No tienes un vehículo registrado. Por favor, regístralo antes de reservar."))
                return@launch
            }

            // 2. USAR LOS DATOS DEL VEHÍCULO (usando los nuevos nombres: plate y type)
            val plate = vehicle.plate
            val type = vehicle.type

            val duration = _state.value.bookingConfirmation?.durationHours ?: 1
            val paymentMethod = _state.value.selectedPaymentMethod
            val clientName = sessionManager.currentUser.value?.name ?: "Unknown"

            if (currentUserId != -1) {
                try {
                    val reservationId = confirmReservationUseCase(
                        parkingId = parkingId,
                        driverId = currentUserId,
                        duration = duration,
                        paymentMethod = paymentMethod.name,
                        clientName = clientName,
                        vehiclePlate = plate, // Ahora viene de vehicle.plate
                        vehicleType = type    // Ahora viene de vehicle.type
                    )

                    if (reservationId != null) {
                        observeLiveStatus(reservationId)
                        emit(BookingConfirmationEffect.NavigateToSuccess(reservationId))
                    } else {
                        _state.update { it.copy(isLoading = false) }
                        emit(BookingConfirmationEffect.ShowError("No hay espacios disponibles en este parqueo"))
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false) }
                    emit(BookingConfirmationEffect.ShowError("Error de base de datos: ${e.message}"))
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                emit(BookingConfirmationEffect.ShowError("Sesión expirada"))
            }
        }
    }

    private fun emit(effect: BookingConfirmationEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}