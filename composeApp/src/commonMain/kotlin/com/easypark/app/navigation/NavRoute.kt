package com.easypark.app.navigation

import kotlinx.serialization.Serializable

sealed class NavRoute {
    @Serializable
    object SignIn: NavRoute()

    @Serializable
    object  Register: NavRoute()

    @Serializable
    object RegisterVehicle: NavRoute()

    @Serializable
    object RegisterParking: NavRoute()

    @Serializable
    object SpaceManagement: NavRoute()

    @Serializable
    object Notifications: NavRoute()

    @Serializable
    object Earnings: NavRoute()

    @Serializable
    object ReservationHistory: NavRoute()
}
