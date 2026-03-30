package com.easypark.app.registerparking.presentation.state

sealed interface RegisterParkingEvent {
    data class OnNameChanged(val value: String) : RegisterParkingEvent
    data class OnAddressChanged(val value: String) : RegisterParkingEvent
    data class OnPriceChanged(val value: String) : RegisterParkingEvent
    data class OnSpacesChanged(val value: String) : RegisterParkingEvent
    data class OnLocationChanged(val lat: Double, val lng: Double) : RegisterParkingEvent
    object OnClickRegister : RegisterParkingEvent
    object OnClickBack : RegisterParkingEvent
}