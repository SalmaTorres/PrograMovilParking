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

//    @Serializable
//    data object ParkingDetails: NavRoute()
//
//    @Serializable
//    data object BookingConfirmation: NavRoute()
//
//    @Serializable
//    data object Notifications: NavRoute()
}
