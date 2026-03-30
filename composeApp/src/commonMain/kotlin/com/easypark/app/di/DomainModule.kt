package com.easypark.app.di

import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingConfirmationUseCase
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetParkingDetailUseCase(get()) }
    factory { GetBookingConfirmationUseCase(get()) }
}