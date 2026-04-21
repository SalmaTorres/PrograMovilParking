package com.easypark.app.parkingdetails.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import com.easypark.app.parkingdetails.domain.usecase.RateParkingUseCase
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsEffect
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsEvent
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParkingDetailsViewModel(
    private val parkingId: Int,
    private val getParkingDetailUseCase: GetParkingDetailUseCase,
    private val rateParkingUseCase: RateParkingUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(ParkingDetailsUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ParkingDetailsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadParkingDetail()
    }

    fun onEvent(event: ParkingDetailsEvent) {
        when (event) {
            ParkingDetailsEvent.OnBackClick -> emit(ParkingDetailsEffect.NavigateBack)
            ParkingDetailsEvent.OnReserveClick -> {
                _state.value.parkingDetail?.let {
                    emit(ParkingDetailsEffect.NavigateToBooking(it.id))
                }
            }
            ParkingDetailsEvent.OnLoadDetail -> loadParkingDetail()
            is ParkingDetailsEvent.OnRate -> {
                _state.update { it.copy(userRating = event.rating) }
                performRating(event.rating)
            }
        }
    }

    private fun loadParkingDetail() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val detail = getParkingDetailUseCase(parkingId)
                _state.update { it.copy(
                    isLoading = false,
                    parkingDetail = detail) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                emit(ParkingDetailsEffect.ShowError("Error al conectar con la base de datos"))
            }
        }
    }

    private fun performRating(stars: Int) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()

            if (userId != -1) {
                rateParkingUseCase(parkingId, userId, stars.toFloat())
                loadParkingDetail()
            } else {
                emit(ParkingDetailsEffect.ShowError("Debes iniciar sesión para calificar"))
            }
        }
    }

    private fun emit(effect: ParkingDetailsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}