package com.easypark.app.earnings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.earnings.domain.usecase.GetEarningsDataUseCase
import com.easypark.app.earnings.presentation.state.EarningsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EarningsViewModel(
    private val sessionManager: SessionManager,
    private val getEarningsDataUseCase: GetEarningsDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EarningsUiState())
    val state = _state.asStateFlow()

    init {
        loadEarningsData()
    }

    private fun loadEarningsData() {
        viewModelScope.launch {
            val user = sessionManager.currentUser.value
            val parkingId = sessionManager.currentParkingId

            if (parkingId != null) {
                _state.update { it.copy(
                    isLoading = true,
                    parkingName = user?.name ?: "Mi Parqueo"
                )}
                try {
                    val data = getEarningsDataUseCase.execute(parkingId)
                    _state.update { it.copy(
                        isLoading = false,
                        summary = data.summary,
                        transactions = data.history
                    )}
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            } else {
                _state.update { it.copy(errorMessage = "No se encontró el parqueo") }
            }
        }
    }
}
