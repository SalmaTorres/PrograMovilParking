package com.easypark.app.earnings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.earnings.domain.usecase.GetEarningsDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EarningsViewModel(
    private val getEarningsDataUseCase: GetEarningsDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EarningsUiState())
    val state = _state.asStateFlow()

    init {
        loadEarningsData()
    }

    private fun loadEarningsData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val data = getEarningsDataUseCase.execute()
                _state.update {
                    it.copy(
                        isLoading = false,
                        summary = data.summary,
                        transactions = data.history
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
}
