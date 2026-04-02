package com.easypark.app.navigation

import kotlinx.serialization.Serializable

sealed class NavRoute {
    @Serializable object SignIn: NavRoute()
    @Serializable object  Register: NavRoute()
    @Serializable object RegisterParking: NavRoute()
    @Serializable object SpaceManagement: NavRoute()
    @Serializable object Notifications: NavRoute()
    @Serializable object Earnings: NavRoute()
    @Serializable object ReservationHistory: NavRoute()
    @Serializable object FindParking: NavRoute()
    @Serializable object ReservationSummary: NavRoute()
    @Serializable data class ParkingDetails(val id: String) : NavRoute()
    @Serializable data class BookingConfirmation(val id: String) : NavRoute()
}
