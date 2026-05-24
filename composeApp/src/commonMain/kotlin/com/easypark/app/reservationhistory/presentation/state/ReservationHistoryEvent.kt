package com.easypark.app.reservationhistory.presentation.state

sealed interface ReservationHistoryEvent {
    data class OnCheckInClick(val reservationId: Int) : ReservationHistoryEvent
}