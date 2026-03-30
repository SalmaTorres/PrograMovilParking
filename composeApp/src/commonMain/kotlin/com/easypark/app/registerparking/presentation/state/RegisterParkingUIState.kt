package com.easypark.app.registerparking.presentation.state

data class RegisterParkingUIState (
    val name: String = "",
    val address: String = "",
    val latitude: Double = -16.5000,
    val longitude: Double = -68.1500,
    val pricePerHour: String = "",
    val totalSpaces: String = "",
    val isLoading: Boolean = false,
    val isNameError: Boolean = false,
    val isAddressError: Boolean = false,
    val isPriceError: Boolean = false,
    val isSpacesError: Boolean = false,
)