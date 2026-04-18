package com.easypark.app.parkingdetails.presentation.state

sealed interface ParkingDetailsEffect {
    data object NavigateBack : ParkingDetailsEffect
    data class NavigateToBooking(val id: Int) : ParkingDetailsEffect
    data class ShowError(val message: String) : ParkingDetailsEffect
}