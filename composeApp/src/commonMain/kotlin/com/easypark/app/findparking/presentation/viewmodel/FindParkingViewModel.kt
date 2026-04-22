package com.easypark.app.findparking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.findparking.domain.repository.FindParkingRepository
import com.easypark.app.findparking.domain.usecase.GetParkingsUseCase
import com.easypark.app.findparking.presentation.state.FindParkingEffect
import com.easypark.app.findparking.presentation.state.FindParkingEvent
import com.easypark.app.findparking.presentation.state.FindParkingUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FindParkingViewModel(
    private val repository: FindParkingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FindParkingUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FindParkingEffect>()
    val effect = _effect.asSharedFlow()

    init {
        startRealtimeTracking()
    }

    fun onEvent(event: FindParkingEvent) {
        when (event) {
            is FindParkingEvent.OnQueryChanged -> {
                val query = event.query
                if (query.isEmpty()) {
                    _state.update { it.copy(
                        searchQuery = "",
                        suggestions = emptyList(),
                        selectedParking = null
                    ) }
                } else {
                    val filtered = _state.value.allParkings.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    _state.update { it.copy(searchQuery = query, suggestions = filtered) }
                }
            }

            is FindParkingEvent.OnSuggestionSelected -> {
                _state.update { it.copy(
                    searchQuery = event.parking.name,
                    suggestions = emptyList(),
                    selectedParking = event.parking
                ) }
                emit(FindParkingEffect.MoveCamera(event.parking.latitude, event.parking.longitude))
            }

            is FindParkingEvent.OnMarkerClicked -> {
                _state.update { it.copy(selectedParking = event.parking) }
            }

            FindParkingEvent.OnDismissDetails -> {
                _state.update { it.copy(selectedParking = null) }
            }

            FindParkingEvent.OnReserveClick -> {
                _state.value.selectedParking?.let { emit(FindParkingEffect.NavigateToBooking(it.id)) }
            }

            FindParkingEvent.OnDetailsClick -> {
                _state.value.selectedParking?.let { emit(FindParkingEffect.NavigateToDetails(it.id)) }
            }
        }
    }

    private fun startRealtimeTracking() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.observeParkingsRealtime().collect { updatedList ->
                _state.update { it.copy(
                    allParkings = updatedList,
                    isLoading = false
                )}

                _state.value.selectedParking?.let { selected ->
                    val updated = updatedList.find { it.id == selected.id }
                    if (updated != null) {
                        _state.update { it.copy(selectedParking = updated) }
                    }
                }
            }
        }
    }

    private fun emit(effect: FindParkingEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}