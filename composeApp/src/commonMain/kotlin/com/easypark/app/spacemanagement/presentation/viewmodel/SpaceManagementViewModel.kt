package com.easypark.app.spacemanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.spacemanagement.domain.usecase.GetSpaceDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpaceManagementViewModel(
    private val getSpaceDataUseCase: GetSpaceDataUseCase
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
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val summary = getSpaceDataUseCase.getSummary()
                val spots = getSpaceDataUseCase.getSpots()
                _state.update {
                    it.copy(
                        isLoading = false,
                        summary = summary,
                        parkingSpots = spots
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar los datos."
                    )
                }
            }
        }
    }
}