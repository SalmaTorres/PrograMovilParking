package com.easypark.app.di

<<<<<<< HEAD
import com.easypark.app.spacemanagement.data.SpaceManagementMockRepository
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SpaceManagementMockRepository) { bind<SpaceManagementRepository>() }
=======
import com.easypark.app.bookingconfirmation.data.repository.FakeBookingConfirmationRepositoryImpl
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.parkingdetails.data.repository.FakeParkingDetailRepositoryImpl
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailRepository
import org.koin.dsl.module

val dataModule = module {
    single<ParkingDetailRepository> { FakeParkingDetailRepositoryImpl() }
    single<BookingConfirmationRepository> { FakeBookingConfirmationRepositoryImpl() }
>>>>>>> origin/Carlita
}