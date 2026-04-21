package com.easypark.app.di

import com.easypark.app.bookingconfirmation.domain.usecase.ConfirmReservationUseCase
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingInfoUseCase
import com.easypark.app.findparking.domain.usecase.GetParkingsUseCase
import com.easypark.app.notifications.domain.usecase.GetNotificationsUseCase
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import com.easypark.app.parkingdetails.domain.usecase.RateParkingUseCase
import org.koin.dsl.module
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.registerparking.domain.usecase.RegisterParkingUseCase
import com.easypark.app.registervehicle.domain.usecase.RegisterVehicleUseCase
import com.easypark.app.signin.domain.usecase.DoLoginUseCase
import com.easypark.app.reservationsummary.domain.usecase.GetReservationSummaryUseCase
import org.koin.core.module.dsl.singleOf

val domainModule = module {
    singleOf(::DoLoginUseCase)
    singleOf(::DoRegisterUseCase)
    singleOf(::RegisterParkingUseCase)
    singleOf(::RegisterVehicleUseCase)
    singleOf(::GetParkingsUseCase)
    singleOf(::GetParkingDetailUseCase)
    singleOf(::RateParkingUseCase)
    singleOf(::GetBookingInfoUseCase)
    singleOf(::ConfirmReservationUseCase)
    singleOf(::GetNotificationsUseCase)
    singleOf ( ::GetReservationSummaryUseCase)
}
