package com.easypark.app.parkingdetails.presentation.state

sealed interface ParkingDetailsEvent {
    object OnBackClick : ParkingDetailsEvent
    object OnReserveClick : ParkingDetailsEvent
    object OnRateClick : ParkingDetailsEvent
}