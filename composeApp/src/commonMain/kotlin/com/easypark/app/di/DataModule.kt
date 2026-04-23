package com.easypark.app.di

import com.easypark.app.bookingconfirmation.data.datasource.BookingConfirmationLocalDataSource
import com.easypark.app.bookingconfirmation.data.repository.BookingConfirmationRepositoryImpl
import com.easypark.app.bookingconfirmation.data.service.BookingConfirmationDbService
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.core.data.datasource.ReservationLocalDataSource
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.db.AppDatabase
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.core.data.service.ReservationDbService
import com.easypark.app.core.data.service.SpaceDbService
import com.easypark.app.core.data.sync.SyncManager
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.earnings.data.repository.EarningsRepositoryImpl
import com.easypark.app.earnings.domain.repository.EarningsRepository
import com.easypark.app.findparking.data.datasource.FindParkingLocalDataSource
import com.easypark.app.findparking.data.repository.FindParkingRepositoryImpl
import com.easypark.app.findparking.data.service.FindParkingDbService
import com.easypark.app.findparking.domain.repository.FindParkingRepository
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import com.easypark.app.notifications.data.repository.NotificationsRepositoryImpl
import com.easypark.app.notifications.data.service.NotificationDbService
import com.easypark.app.notifications.domain.repository.NotificationsRepository
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
import com.easypark.app.reservationhistory.data.repository.ReservationHistoryRepositoryImpl
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource
import com.easypark.app.reservationsummary.data.repository.ReservationSummaryRepositoryImpl
import com.easypark.app.reservationsummary.data.service.ReservationSummaryDbService
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
import com.easypark.app.signin.data.datasource.SignInLocalDataSource
import com.easypark.app.signin.data.repository.AuthRepositoryImpl
import com.easypark.app.signin.data.service.SignInDbService
import com.easypark.app.signin.domain.repository.AuthRepository
import com.easypark.app.spacemanagement.data.repository.SpaceManagementRepositoryImpl
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SessionManager)
    singleOf(::FirebaseManager)

    singleOf(::RegisterDbService).bind<RegisterLocalDataSource>()
    singleOf(::SignInDbService).bind<SignInLocalDataSource>()
    singleOf(::RegisterParkingDbService).bind<RegisterParkingLocalDataSource>()
    singleOf(::RegisterVehicleDbService).bind<RegisterVehicleLocalDataSource>()
    singleOf(::FindParkingDbService).bind<FindParkingLocalDataSource>()
    singleOf(::ParkingDetailsDbService).bind<ParkingDetailsLocalDataSource>()
    singleOf(::BookingConfirmationDbService).bind<BookingConfirmationLocalDataSource>()
    singleOf(::SpaceDbService).bind<SpaceLocalDataSource>()
    singleOf(::ReservationSummaryDbService).bind<ReservationSummaryLocalDataSource>()
    singleOf(::ReservationDbService).bind<ReservationLocalDataSource>()
    singleOf(::NotificationDbService).bind<NotificationLocalDataSource>()

    singleOf(::RegisterRepositoryImpl).bind<RegisterRepository>()
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::RegisterParkingRepositoryImpl).bind<RegisterParkingRepository>()
    singleOf(::RegisterVehicleRepositoryImpl).bind<RegisterVehicleRepository>()
    singleOf(::FindParkingRepositoryImpl).bind<FindParkingRepository>()
    singleOf(::ParkingDetailRepositoryImpl).bind<ParkingDetailsRepository>()
    singleOf(::BookingConfirmationRepositoryImpl).bind<BookingConfirmationRepository>()
    singleOf(::ReservationSummaryRepositoryImpl).bind<ReservationSummaryRepository>()
    singleOf(::SpaceManagementRepositoryImpl).bind<SpaceManagementRepository>()
    singleOf(::EarningsRepositoryImpl).bind<EarningsRepository>()
    singleOf(::ReservationHistoryRepositoryImpl).bind<ReservationHistoryRepository>()
    singleOf(::NotificationsRepositoryImpl).bind<NotificationsRepository>()

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().parkingDao() }
    single { get<AppDatabase>().notificationDao() }
    single { get<AppDatabase>().spaceDao() }
    single { get<AppDatabase>().reservationSummaryDao() }
    single { get<AppDatabase>().findParkingDao() }
    single { get<AppDatabase>().registerParkingDao() }
    single { get<AppDatabase>().reservationHistoryDao() }
    single { get<AppDatabase>().signInDao() }
    single { get<AppDatabase>().registerDao() }
    single { get<AppDatabase>().registerVehicleDao() }
    single { get<AppDatabase>().reservationDao() }
    single { get<AppDatabase>().bookingConfirmationDao() }

    singleOf(::RemoteConfigManager)
    single { get<AppDatabase>().appEventDao() }
    singleOf(::BookingConfirmationRepositoryImpl).bind<BookingConfirmationRepository>()
}