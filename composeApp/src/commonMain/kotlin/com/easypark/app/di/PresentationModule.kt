package com.easypark.app.di

import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.notifications.presentation.viewmodel.NotificationsViewModel
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import com.easypark.app.register.presentation.viewmodel.RegisterViewModel
import com.easypark.app.signin.domain.model.LoginModel
import com.easypark.app.signin.domain.repository.AuthenticationRepository
import com.easypark.app.signin.presentation.screen.SignInScreen
import com.easypark.app.signin.presentation.viewmodel.SignInViewModel
import com.easypark.app.spacemanagement.presentation.viewmodel.SpaceManagementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::RegisterViewModel)


    viewModelOf(::NotificationsViewModel)
    viewModelOf(::ParkingDetailsViewModel)
    viewModelOf(::BookingConfirmationViewModel)
    viewModelOf(::SpaceManagementViewModel)

}