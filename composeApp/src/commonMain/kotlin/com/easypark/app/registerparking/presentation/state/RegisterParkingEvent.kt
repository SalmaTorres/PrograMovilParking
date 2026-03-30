package com.easypark.app.registerparking.presentation.state

sealed interface RegisterParkingEvent {
    data class OnNameChanged(val name: String) : RegisterParkingEvent
    data class OnAddressChanged(val address: String) : RegisterParkingEvent
    data class OnPriceChanged(val price: String) : RegisterParkingEvent
    data class OnSpacesChanged(val spaces: String) : RegisterParkingEvent
    data class OnLocationChanged(val lat: Double, val lng: Double) : RegisterParkingEvent
    object OnClickRegister : RegisterParkingEvent
    object OnClickBack : RegisterParkingEvent
}