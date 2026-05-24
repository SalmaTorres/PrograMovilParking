package com.easypark.app.registervehicle.presentation.state

import com.easypark.app.core.domain.model.status.VehicleType

data class RegisterVehicleUIState(
    val plate: String = "",
    val type: VehicleType = VehicleType.AUTOMOVIL,

    val isPlateError: Boolean = false,

    val isLoading: Boolean = false
)