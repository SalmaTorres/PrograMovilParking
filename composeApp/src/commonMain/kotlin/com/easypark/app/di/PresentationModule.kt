package com.easypark.app.di

import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.earnings.presentation.viewmodel.EarningsViewModel
import com.easypark.app.findparking.presentation.viewmodel.FindParkingViewModel
import com.easypark.app.notifications.presentation.viewmodel.NotificationsViewModel
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import com.easypark.app.register.presentation.viewmodel.RegisterViewModel
import com.easypark.app.registerparking.presentation.viewmodel.RegisterParkingViewModel
import com.easypark.app.registervehicle.presentation.viewmodel.RegisterVehicleViewModel
import com.easypark.app.reservationhistory.presentation.viewmodel.ReservationHistoryViewModel
import com.easypark.app.reservationsummary.presentation.viewmodel.ReservationSummaryViewModel
import com.easypark.app.signin.presentation.viewmodel.SignInViewModel
import com.easypark.app.spacemanagement.presentation.viewmodel.SpaceManagementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val presentationModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterParkingViewModel)
    viewModelOf(::SpaceManagementViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::EarningsViewModel)
    viewModelOf(::ReservationHistoryViewModel)
    viewModelOf(::FindParkingViewModel)
    viewModelOf(::RegisterVehicleViewModel)
    viewModel { (parkingId: Int) ->
        ParkingDetailsViewModel(parkingId, get(), get(), get())
    }
    viewModel { (parkingId: Int) ->
        BookingConfirmationViewModel(parkingId, get(), get(), get())
    }
    viewModelOf(::ReservationSummaryViewModel)
}