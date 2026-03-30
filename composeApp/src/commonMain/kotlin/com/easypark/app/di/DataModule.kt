package com.easypark.app.di

import com.easypark.app.bookingconfirmation.data.repository.FakeBookingConfirmationRepositoryImpl
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.parkingdetails.data.repository.FakeParkingDetailRepositoryImpl
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailRepository
import org.koin.dsl.module

val dataModule = module {
    single<ParkingDetailRepository> { FakeParkingDetailRepositoryImpl() }
    single<BookingConfirmationRepository> { FakeBookingConfirmationRepositoryImpl() }
}