package com.easypark.app.di

import com.easypark.app.earnings.data.repository.MockEarningsRepository
import com.easypark.app.earnings.domain.repository.EarningsRepository
import com.easypark.app.reservationhistory.data.repository.MockReservationHistoryRepository
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.notifications.data.repository.MockNotificationsRepository
import com.easypark.app.notifications.domain.repository.NotificationsRepository
import com.easypark.app.register.data.repository.RegisterRepositoryImpl
import com.easypark.app.register.domain.repository.RegisterRepository
import com.easypark.app.registervehicle.data.repository.RegisterVehicleRepositoryImpl
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import org.koin.dsl.module
import com.easypark.app.core.data.repository.ParkingRepositoryImpl
import com.easypark.app.core.domain.repository.ParkingRepository
import com.easypark.app.signin.data.repository.AuthenticationRepositoryImpl
import com.easypark.app.signin.domain.repository.AuthenticationRepository
import com.easypark.app.spacemanagement.data.repository.SpaceManagementMockRepository
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository

val dataModule = module {
    single<AuthenticationRepository> { AuthenticationRepositoryImpl() }
    single<RegisterRepository> { RegisterRepositoryImpl() }
    single<SpaceManagementRepository> { SpaceManagementMockRepository() }
    single<NotificationsRepository> { MockNotificationsRepository() }
    single<ParkingRepository> { ParkingRepositoryImpl() }
    single<EarningsRepository> { MockEarningsRepository() }
    single<ReservationHistoryRepository> { MockReservationHistoryRepository() }
    single<RegisterVehicleRepository> { RegisterVehicleRepositoryImpl() }
}
