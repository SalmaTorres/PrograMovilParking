package com.easypark.app.di

import com.easypark.app.spacemanagement.data.SpaceManagementMockRepository
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.easypark.app.bookingconfirmation.data.repository.FakeBookingConfirmationRepositoryImpl
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.parkingdetails.data.repository.FakeParkingDetailRepositoryImpl
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailRepository
import com.easypark.app.register.data.repository.RegisterRepositoryImpl
import com.easypark.app.register.domain.repository.RegisterRepository
import com.easypark.app.signin.data.repository.AuthenticationRepositoryImpl
import com.easypark.app.signin.domain.repository.AuthenticationRepository

val dataModule = module {
    single<AuthenticationRepository> { AuthenticationRepositoryImpl() }
    single<RegisterRepository> { RegisterRepositoryImpl() }


    singleOf(::SpaceManagementMockRepository) { bind<SpaceManagementRepository>() }
    single<ParkingDetailRepository> { FakeParkingDetailRepositoryImpl() }
    single<BookingConfirmationRepository> { FakeBookingConfirmationRepositoryImpl() }
}