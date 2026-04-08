package com.easypark.app.registervehicle.presentation.state

sealed interface RegisterVehicleEffect {
    object NavigateBack : RegisterVehicleEffect
    object NavigateNext : RegisterVehicleEffect
    data class ShowError(val message: String) : RegisterVehicleEffect
}