package com.easypark.app.di

import com.easypark.app.earnings.presentation.viewmodel.EarningsViewModel
import com.easypark.app.findparking.presentation.viewmodel.FindParkingViewModel
import com.easypark.app.notifications.presentation.viewmodel.NotificationsViewModel
import com.easypark.app.register.presentation.viewmodel.RegisterViewModel
import com.easypark.app.reservationhistory.presentation.viewmodel.ReservationHistoryViewModel
import com.easypark.app.reservationsummary.presentation.viewmodel.ReservationSummaryViewModel
import com.easypark.app.signin.presentation.viewmodel.SignInViewModel
import com.easypark.app.spacemanagement.presentation.viewmodel.SpaceManagementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::SpaceManagementViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::EarningsViewModel)
    viewModelOf(::ReservationHistoryViewModel)
    viewModelOf(::FindParkingViewModel)
    viewModelOf(::ReservationSummaryViewModel)
}