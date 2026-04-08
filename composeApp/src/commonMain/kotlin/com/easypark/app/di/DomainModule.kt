package com.easypark.app.di

import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingConfirmationUseCase
import com.easypark.app.earnings.domain.usecase.GetEarningsDataUseCase
import com.easypark.app.reservationhistory.domain.usecase.GetReservationHistoryUseCase
import com.easypark.app.findparking.domain.usecase.GetParkingsUseCase
import com.easypark.app.notifications.domain.usecase.GetNotificationsUseCase
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import org.koin.dsl.module
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.registerparking.domain.usecase.RegisterParkingUseCase
import com.easypark.app.signin.domain.usecase.DoLoginUseCase
import com.easypark.app.spacemanagement.domain.usecase.GetSpaceDataUseCase

val domainModule = module {
    factory { DoLoginUseCase(get()) }
    factory { DoRegisterUseCase(get()) }
    factory { RegisterParkingUseCase(get()) }
    factory { GetSpaceDataUseCase(get()) }
    factory { GetNotificationsUseCase(get()) }
    factory { GetParkingsUseCase(get()) }
    factory { GetParkingDetailUseCase(get()) }
    factory { GetBookingConfirmationUseCase(get()) }
    factory { GetEarningsDataUseCase(get()) }
    factory { GetReservationHistoryUseCase(get()) }
}