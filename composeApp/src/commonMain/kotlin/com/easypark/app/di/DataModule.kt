package com.easypark.app.di

import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.bookingconfirmation.data.repository.BookingConfirmationRepositoryImpl
import com.easypark.app.bookingconfirmation.data.service.BookingConfirmationDbService
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.db.AppDatabase
import com.easypark.app.core.data.service.SpaceDbService
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.findparking.data.datasource.FindParkingLocalDataSource
import com.easypark.app.findparking.data.repository.FindParkingRepositoryImpl
import com.easypark.app.findparking.data.service.FindParkingDbService
import com.easypark.app.findparking.domain.repository.FindParkingRepository
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource
import com.easypark.app.parkingdetails.data.repository.ParkingDetailRepositoryImpl
import com.easypark.app.parkingdetails.data.service.ParkingDetailsDbService
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailsRepository
import com.easypark.app.register.data.datasource.RegisterLocalDataSource
import com.easypark.app.register.data.repository.RegisterRepositoryImpl
import com.easypark.app.register.data.service.RegisterDbService
import com.easypark.app.register.domain.repository.RegisterRepository
import com.easypark.app.registerparking.data.datasource.RegisterParkingLocalDataSource
import com.easypark.app.registerparking.data.repository.RegisterParkingRepositoryImpl
import com.easypark.app.registerparking.data.service.RegisterParkingDbService
import com.easypark.app.registerparking.domain.repository.RegisterParkingRepository
import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.registervehicle.data.repository.RegisterVehicleRepositoryImpl
import com.easypark.app.registervehicle.data.service.RegisterVehicleDbService
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource
import com.easypark.app.reservationsummary.data.repository.ReservationSummaryRepositoryImpl
import com.easypark.app.reservationsummary.data.service.ReservationSummaryDbService
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
import com.easypark.app.signin.data.datasource.SignInLocalDataSource
import com.easypark.app.signin.data.repository.AuthRepositoryImpl
import com.easypark.app.signin.data.service.SignInDbService
import com.easypark.app.signin.domain.repository.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    // Session
    singleOf(::SessionManager)

    // DataSources / Services
    singleOf(::RegisterDbService).bind<RegisterLocalDataSource>()
    singleOf(::SignInDbService).bind<SignInLocalDataSource>()
    singleOf(::RegisterParkingDbService).bind<RegisterParkingLocalDataSource>()
    singleOf(::RegisterVehicleDbService).bind<RegisterVehicleLocalDataSource>()
    singleOf(::FindParkingDbService).bind<FindParkingLocalDataSource>()
    singleOf(::ParkingDetailsDbService).bind<ParkingDetailsLocalDataSource>()
    singleOf(::BookingConfirmationDbService).bind<BookingConfirmationLocalDataSource>()
    singleOf(::SpaceDbService).bind<SpaceLocalDataSource>()
    singleOf(::ReservationSummaryDbService).bind<ReservationSummaryLocalDataSource>()

    // Repositories
    singleOf(::RegisterRepositoryImpl).bind<RegisterRepository>()
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::RegisterParkingRepositoryImpl).bind<RegisterParkingRepository>()
    singleOf(::RegisterVehicleRepositoryImpl).bind<RegisterVehicleRepository>()
    singleOf(::FindParkingRepositoryImpl).bind<FindParkingRepository>()
    singleOf(::ParkingDetailRepositoryImpl).bind<ParkingDetailsRepository>()
    singleOf(::BookingConfirmationRepositoryImpl).bind<BookingConfirmationRepository>()
    singleOf(::ReservationSummaryRepositoryImpl).bind<ReservationSummaryRepository>()

    // NOTA: Los DAOs se siguen obteniendo de la DB (porque no tienen constructor propio)
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().parkingDao() }
    single { get<AppDatabase>().notificationDao() }
    single { get<AppDatabase>().spaceDao() }
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().reservationSummaryDao() }
    single { get<AppDatabase>().findParkingDao() }
    single { get<AppDatabase>().registerParkingDao() }
    single { get<AppDatabase>().reservationHistoryDao() }
    single { get<AppDatabase>().signInDao() }
    single { get<AppDatabase>().registerDao() }
}