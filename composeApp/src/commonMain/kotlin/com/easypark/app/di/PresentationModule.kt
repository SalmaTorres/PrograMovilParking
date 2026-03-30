package com.easypark.app.di

import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.notifications.presentation.viewmodel.NotificationsViewModel
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import com.easypark.app.spacemanagement.presentation.viewmodel.SpaceManagementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::ParkingDetailsViewModel)
    viewModelOf(::BookingConfirmationViewModel)
    viewModelOf(::SpaceManagementViewModel)
}
