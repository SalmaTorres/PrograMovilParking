package com.easypark.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    
    @Serializable
    object ParkingDetails: NavRoute()
    
    @Serializable
    object BookingConfirmation: NavRoute()
}
