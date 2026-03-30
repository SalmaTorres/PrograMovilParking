package com.easypark.app.parkingdetails.presentation.state

sealed interface ParkingDetailsEffect {
    object NavigateBack : ParkingDetailsEffect
}