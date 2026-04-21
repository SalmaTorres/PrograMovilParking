package com.easypark.app.navigation

import com.easypark.app.core.domain.model.UserModel
import kotlinx.serialization.Serializable

sealed class NavRoute {
    @Serializable object SignIn: NavRoute()
    @Serializable object  Register: NavRoute()
    @Serializable data class RegisterParking(val userFromStep1: UserModel) : NavRoute()
    @Serializable data class RegisterVehicle(val userFromStep1: UserModel) : NavRoute()
    @Serializable object SpaceManagement: NavRoute()
    @Serializable object Notifications: NavRoute()
    @Serializable object Earnings: NavRoute()
    @Serializable object ReservationHistory: NavRoute()
    @Serializable object FindParking: NavRoute()
    @Serializable data class ReservationSummary(val id: Int): NavRoute()
    @Serializable data class ParkingDetails(val id: Int) : NavRoute()
    @Serializable data class BookingConfirmation(val id: Int) : NavRoute()
}
