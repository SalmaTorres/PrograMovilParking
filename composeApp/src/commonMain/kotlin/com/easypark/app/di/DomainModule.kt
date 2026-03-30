package com.easypark.app.di

import com.easypark.app.spacemanagement.domain.usecase.GetSpaceDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingConfirmationUseCase
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.registerparking.domain.usecase.RegisterParkingUseCase
import com.easypark.app.signin.domain.usecase.DoLoginUseCase

val domainModule = module {
    factory { DoLoginUseCase(get()) }
    factory { DoRegisterUseCase(get()) }

    factoryOf(::GetSpaceDataUseCase)
    factory { GetParkingDetailUseCase(get()) }
    factory { GetBookingConfirmationUseCase(get()) }
    factoryOf(::RegisterParkingUseCase)
}