package com.easypark.app.findparking.presentation.state

import com.easypark.app.core.domain.model.ParkingModel

sealed interface FindParkingEvent {
    data class OnQueryChanged(val query: String) : FindParkingEvent
    data class OnSuggestionSelected(val parking: ParkingModel) : FindParkingEvent
    data class OnMarkerClicked(val parking: ParkingModel) : FindParkingEvent
    object OnDismissDetails : FindParkingEvent
    object OnReserveClick : FindParkingEvent
    object OnDetailsClick : FindParkingEvent
}