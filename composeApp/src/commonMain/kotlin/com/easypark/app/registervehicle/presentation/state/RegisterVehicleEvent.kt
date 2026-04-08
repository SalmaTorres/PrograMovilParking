package com.easypark.app.registervehicle.presentation.state

sealed interface RegisterVehicleEvent {

    data class OnPlateChange(val plate: String) : RegisterVehicleEvent
    data class OnModelChange(val model: String) : RegisterVehicleEvent
    data class OnColorChange(val color: String) : RegisterVehicleEvent

    object OnSubmitClick : RegisterVehicleEvent
    object OnBackClick : RegisterVehicleEvent
}