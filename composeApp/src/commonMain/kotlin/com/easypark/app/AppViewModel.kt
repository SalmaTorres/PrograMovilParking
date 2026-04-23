package com.easypark.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AppUIState(
    val maintenanceMessage: String = ""
)

class AppViewModel(
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUIState())
    val uiState = _uiState.asStateFlow()

    init {
        // Enlaza con Remote Config (Paso 5 de la guía)
        observeConfig("mensaje_mantenimiento")
    }

    private fun observeConfig(key: String) {
        viewModelScope.launch {
            remoteConfigRepository.observeConfig(key).collectLatest { valor ->
                _uiState.value = _uiState.value.copy(
                    maintenanceMessage = valor ?: ""
                )
            }
        }
    }
}
