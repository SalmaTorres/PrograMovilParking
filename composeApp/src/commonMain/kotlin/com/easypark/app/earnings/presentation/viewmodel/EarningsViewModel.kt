package com.easypark.app.earnings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.earnings.domain.usecase.GetEarningsDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class EarningsViewModel(
    private val getEarningsDataUseCase: GetEarningsDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EarningsUiState())
    val state: StateFlow<EarningsUiState> = _state.asStateFlow()

    init {
        loadEarningsData()
    }

    private fun loadEarningsData() {
        getEarningsDataUseCase()
            .onEach { data ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    summary = data.summary,
                    transactions = data.history
                )
            }
            .catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
            .launchIn(viewModelScope)
    }
}
