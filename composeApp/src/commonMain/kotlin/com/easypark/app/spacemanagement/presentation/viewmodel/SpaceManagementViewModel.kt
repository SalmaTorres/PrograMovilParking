package com.easypark.app.spacemanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementEffect
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpaceManagementViewModel(
    private val repository: SpaceManagementRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(SpaceManagementUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SpaceManagementEffect>()
    val effect = _effect.asSharedFlow()

    init {
        startRealtimeMonitoring()
    }

    private fun startRealtimeMonitoring() {
        val myParkingId = sessionManager.currentParkingId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.observeParkingSpots(myParkingId).collect { liveSpots ->
                val total = liveSpots.size
                val occupied = liveSpots.count { it.isOccupied }

                _state.update { it.copy(
                    isLoading = false,
                    parkingSpots = liveSpots,
                    summary = SpaceSummary(total, occupied, total - occupied)
                )}
                println("LOG TIEMPO REAL: Los espacios han cambiado en Firebase")
            }
        }
    }
}