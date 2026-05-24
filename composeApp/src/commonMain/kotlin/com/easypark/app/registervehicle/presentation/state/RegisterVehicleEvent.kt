package com.easypark.app.registervehicle.presentation.state

import com.easypark.app.core.domain.model.status.VehicleType

sealed interface RegisterVehicleEvent {

    data class OnPlateChange(val plate: String) : RegisterVehicleEvent
    data class OnTypeChange(val type: VehicleType) : RegisterVehicleEvent

    object OnSubmitClick : RegisterVehicleEvent
    object OnBackClick : RegisterVehicleEvent
}