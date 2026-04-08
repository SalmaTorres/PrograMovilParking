package com.easypark.app.registervehicle.presentation.state

data class RegisterVehicleUIState(
    val plate: String = "",
    val model: String = "",
    val color: String = "",

    val isPlateError: Boolean = false,
    val isModelError: Boolean = false,
    val isColorError: Boolean = false,

    val isLoading: Boolean = false
)