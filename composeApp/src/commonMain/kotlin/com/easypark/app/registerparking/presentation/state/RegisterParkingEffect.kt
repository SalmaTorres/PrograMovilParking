package com.easypark.app.registerparking.presentation.state

sealed interface RegisterParkingEffect {
    object NavigateBack : RegisterParkingEffect
    object NavigateToSuccess : RegisterParkingEffect
    data class ShowError(
        val message: String
    ) : RegisterParkingEffect
}