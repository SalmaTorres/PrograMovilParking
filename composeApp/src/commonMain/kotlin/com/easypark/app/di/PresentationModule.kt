package com.easypark.app.di

import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::ParkingDetailsViewModel)
    viewModelOf(::BookingConfirmationViewModel)
}