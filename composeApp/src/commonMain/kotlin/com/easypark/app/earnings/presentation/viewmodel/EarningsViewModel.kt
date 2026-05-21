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
                _state.update { currentState ->
                    val totalToday = liveTransactions
                        .filter { it.date.contains("Hace") || it.date.contains("segundos") || it.date.contains("min") || it.date.contains("h") } // Simplistic today filter
                        .sumOf { it.amount }

                    val updatedSummary = currentState.summary?.copy(totalEarnings = totalToday) ?: com.easypark.app.earnings.domain.model.EarningsSummaryModel(
                        totalEarnings = totalToday,
                        percentageChange = 0.0,
                        activeReservations = 0,
                        reservationChange = 0,
                        occupiedSpaces = 0,
                        totalSpaces = 0,
                        isCapacityLimited = false
                    )

                    currentState.copy(
                        transactions = liveTransactions,
                        summary = updatedSummary
                    )
                }
            }
        }
    }
}
