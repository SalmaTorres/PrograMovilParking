package com.easypark.app.di

<<<<<<< HEAD
import com.easypark.app.spacemanagement.domain.usecase.GetSpaceDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetSpaceDataUseCase)
=======
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingConfirmationUseCase
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetParkingDetailUseCase(get()) }
    factory { GetBookingConfirmationUseCase(get()) }
>>>>>>> origin/Carlita
}