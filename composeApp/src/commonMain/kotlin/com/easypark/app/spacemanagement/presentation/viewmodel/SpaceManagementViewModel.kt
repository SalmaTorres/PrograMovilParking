package com.easypark.app.spacemanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.spacemanagement.domain.usecase.GetSpaceDataUseCase
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementEffect
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementEvent
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpaceManagementViewModel(
    private val getSpaceDataUseCase: GetSpaceDataUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(SpaceManagementUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SpaceManagementEffect>()
    val effect = _effect.asSharedFlow()

    init {
        onEvent(SpaceManagementEvent.LoadData)
    }

    fun onEvent(event: SpaceManagementEvent) {
        when (event) {
            is SpaceManagementEvent.LoadData -> loadSpaceData()
        }
    }

    private fun loadSpaceData() {
        viewModelScope.launch {
            val myParkingId = sessionManager.currentParkingId

            if (myParkingId != null) {
                _state.update { it.copy(isLoading = true) }
                try {
                    val summary = getSpaceDataUseCase.getSummary(myParkingId)
                    val spots = getSpaceDataUseCase.getSpots(myParkingId)
                    _state.update {
                        it.copy(isLoading = false, summary = summary, parkingSpots = spots)
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            } else {
                _state.update { it.copy(errorMessage = "No tienes un parqueo registrado") }
            }
        }
    }
}