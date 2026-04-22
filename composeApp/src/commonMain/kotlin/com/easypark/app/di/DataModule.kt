package com.easypark.app.di

import com.easypark.app.core.data.db.AppDatabase
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.core.data.repository.AppEventRepository
import com.easypark.app.core.data.repository.FormDraftRepository
import com.easypark.app.signin.data.datasource.SignInLocalDataSource
import com.easypark.app.signin.data.service.SignInDbService
import com.easypark.app.signin.domain.repository.AuthRepository
import com.easypark.app.signin.data.repository.AuthRepositoryImpl
import com.easypark.app.register.domain.repository.RegisterRepository
import com.easypark.app.register.data.repository.RegisterRepositoryImpl
import com.easypark.app.register.data.datasource.RegisterLocalDataSource
import com.easypark.app.register.data.service.RegisterDbService
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import com.easypark.app.registervehicle.data.repository.RegisterVehicleRepositoryImpl
import com.easypark.app.registervehicle.data.datasource.RegisterVehicleLocalDataSource
import com.easypark.app.registervehicle.data.service.RegisterVehicleDbService
import com.easypark.app.registerparking.domain.repository.RegisterParkingRepository
import com.easypark.app.registerparking.data.repository.RegisterParkingRepositoryImpl
import com.easypark.app.core.domain.session.SessionManager
import org.koin.dsl.module

val dataModule = module {
    single { getDatabaseBuilder(get()) }
    single { createDatabase(get()) }

    // DAOs
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().registerDao() }
    single { get<AppDatabase>().registerVehicleDao() }
    single { get<AppDatabase>().signInDao() }
    single { get<AppDatabase>().parkingDao() }
    single { get<AppDatabase>().findParkingDao() }
    single { get<AppDatabase>().reservationHistoryDao() }
    single { get<AppDatabase>().reservationSummaryDao() }
    single { get<AppDatabase>().notificationDao() }
    single { get<AppDatabase>().spaceDao() }
    single { get<AppDatabase>().reservationDao() }
    single { get<AppDatabase>().bookingConfirmationDao() }
    single { get<AppDatabase>().appEventDao() }
    single { get<AppDatabase>().formDraftDao() }

    // Managers & Services
    single { FirebaseManager() }
    single { RemoteConfigManager() }
    single { SessionManager() }
    single<SignInLocalDataSource> { SignInDbService(get()) }
    single<RegisterLocalDataSource> { RegisterDbService(get()) }
    single<RegisterVehicleLocalDataSource> { RegisterVehicleDbService(get()) }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<RegisterRepository> { RegisterRepositoryImpl(get(), get()) }
    single<RegisterVehicleRepository> { RegisterVehicleRepositoryImpl(get(), get()) }
    single<RegisterParkingRepository> { RegisterParkingRepositoryImpl(get(), get()) }
    single { AppEventRepository(get(), get(), get()) }
    single { FormDraftRepository(get()) }
}
