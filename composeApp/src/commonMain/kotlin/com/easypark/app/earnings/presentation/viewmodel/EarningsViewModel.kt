package com.easypark.app.earnings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.earnings.domain.repository.EarningsRepository
import com.easypark.app.earnings.domain.usecase.GetEarningsDataUseCase
import com.easypark.app.earnings.presentation.state.EarningsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EarningsViewModel(
    private val sessionManager: SessionManager,
    private val repository: EarningsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EarningsUiState())
    val state = _state.asStateFlow()

    init {
        startRealtimeObservation()
    }

    private fun startRealtimeObservation() {
        val parkingId = sessionManager.currentParkingId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Esta es la fuente de verdad: el nodo summary en Firebase
            repository.observeEarningsRealtime(parkingId).collect { liveSummary ->
                _state.update { it.copy(
                    isLoading = false,
                    summary = liveSummary ?: it.summary,
                    parkingName = sessionManager.currentUser.value?.name ?: "Mi Parqueo"
                )}
            }
        }

        viewModelScope.launch {
            repository.observeTransactionsRealtime(parkingId).collect { liveTransactions ->
                _state.update { it.copy(transactions = liveTransactions) }
            }
        }
    }
}
