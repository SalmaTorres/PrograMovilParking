package com.easypark.app.parkingdetails.presentation.state

sealed interface ParkingDetailsEvent {
    object OnBackClick : ParkingDetailsEvent
    object OnReserveClick : ParkingDetailsEvent
    data object OnLoadDetail : ParkingDetailsEvent
    data class OnRate(val rating: Int) : ParkingDetailsEvent
}