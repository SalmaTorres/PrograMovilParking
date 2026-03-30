package com.easypark.app.parkingdetails.presentation.viewmodel

import androidx.lifecycle.ViewModel
<<<<<<< HEAD

class ParkingDetailsViewModel : ViewModel() {
=======
import androidx.lifecycle.viewModelScope
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParkingDetailsViewModel(
    private val getParkingDetailUseCase: GetParkingDetailUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ParkingDetailsUIState())
    val state = _state.asStateFlow()

    init {
        loadParkingDetail("1")
    }

    private fun loadParkingDetail(id: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val detail = getParkingDetailUseCase(id)
            _state.update {
                it.copy(isLoading = false, parkingDetail = detail)
            }
        }
    }
>>>>>>> origin/Carlita
}