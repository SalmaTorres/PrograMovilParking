package com.easypark.app.findparking.presentation.state

sealed interface FindParkingEffect {
    data class NavigateToBooking(val parkingId: Int) : FindParkingEffect
    data class NavigateToDetails(val parkingId: Int) : FindParkingEffect
    data class MoveCamera(val lat: Double, val lng: Double) : FindParkingEffect
}