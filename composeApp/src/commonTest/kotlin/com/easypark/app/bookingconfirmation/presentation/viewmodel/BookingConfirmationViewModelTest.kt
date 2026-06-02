package com.easypark.app.bookingconfirmation.presentation.viewmodel

import ReservationModel
import app.cash.turbine.test
import com.easypark.app.bookingconfirmation.domain.model.BookingConfirmationModel
import com.easypark.app.bookingconfirmation.domain.repository.BookingConfirmationRepository
import com.easypark.app.bookingconfirmation.domain.usecase.ConfirmReservationUseCase
import com.easypark.app.bookingconfirmation.domain.usecase.GetBookingInfoUseCase
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEffect
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.PaymentMethod
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class BookingConfirmationViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initializationLoadsBookingInformation() = runTest {
        val repository = FakeBookingConfirmationRepository(bookingInfo())
        val viewModel = viewModel(repository = repository)

        advanceUntilIdle()

        assertEquals(bookingInfo(), viewModel.state.value.bookingConfirmation)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(listOf(7), repository.bookingInfoRequests)
    }

    @Test
    fun durationChangeRecalculatesTotalCostUsingPricePerHour() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onEvent(BookingConfirmationEvent.OnDurationChange(3))
        advanceUntilIdle()

        val booking = viewModel.state.value.bookingConfirmation
        assertEquals(3, booking?.durationHours)
        assertEquals(36.0, booking?.totalCost)
    }

    @Test
    fun confirmReservationUsesSessionVehicleAndNavigatesToSuccess() = runTest {
        val repository = FakeBookingConfirmationRepository(bookingInfo(), reservationId = 91)
        val viewModel = viewModel(repository = repository, session = signedInSession())
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(BookingConfirmationEvent.OnPaymentMethodSelected(PaymentMethod.QR))
            viewModel.onEvent(BookingConfirmationEvent.OnDurationChange(2))
            viewModel.onEvent(BookingConfirmationEvent.OnConfirmClick)
            advanceUntilIdle()

            assertEquals(BookingConfirmationEffect.NavigateToSuccess(91), awaitItem())
            assertEquals(
                ReservationCall(
                    parkingId = 7,
                    driverId = 12,
                    duration = 2,
                    paymentMethod = "QR",
                    clientName = "Jhessa",
                    vehiclePlate = "ABC-123",
                    vehicleType = "Automovil"
                ),
                repository.reservationCalls.single()
            )
        }
    }

    @Test
    fun confirmReservationWithoutRegisteredVehicleShowsError() = runTest {
        val repository = FakeBookingConfirmationRepository(bookingInfo())
        val viewModel = viewModel(
            repository = repository,
            vehicleRepository = FakeVehicleRepository(vehicle = null),
            session = signedInSession()
        )
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(BookingConfirmationEvent.OnConfirmClick)
            advanceUntilIdle()

            assertIs<BookingConfirmationEffect.ShowError>(awaitItem())
            assertEquals(emptyList(), repository.reservationCalls)
        }
    }

    private fun viewModel(
        repository: FakeBookingConfirmationRepository = FakeBookingConfirmationRepository(bookingInfo()),
        vehicleRepository: FakeVehicleRepository = FakeVehicleRepository(vehicle()),
        session: SessionManager = SessionManager()
    ) = BookingConfirmationViewModel(
        parkingId = 7,
        getBookingInfoUseCase = GetBookingInfoUseCase(repository),
        confirmReservationUseCase = ConfirmReservationUseCase(repository),
        repository = repository,
        vehicleRepository = vehicleRepository,
        sessionManager = session
    )

    private fun bookingInfo() = BookingConfirmationModel(
        parkingId = 7,
        locationName = "EasyPark Centro",
        address = "Av. Ayacucho",
        pricePerHour = 12.0,
        durationHours = 1,
        totalCost = 12.0
    )

    private fun signedInSession() = SessionManager().apply {
        saveSession(UserModel(12, "Jhessa", UserType.DRIVER, "j@easypark.test", "secret", 70000000))
    }

    private fun vehicle() = VehicleModel(
        id = 3,
        driverId = 12,
        plate = "ABC-123",
        type = "Automovil",
        model = "Swift",
        color = "Rojo"
    )

    private data class ReservationCall(
        val parkingId: Int,
        val driverId: Int,
        val duration: Int,
        val paymentMethod: String,
        val clientName: String,
        val vehiclePlate: String,
        val vehicleType: String
    )

    private class FakeBookingConfirmationRepository(
        private val info: BookingConfirmationModel,
        private val reservationId: Int? = 44
    ) : BookingConfirmationRepository {
        val bookingInfoRequests = mutableListOf<Int>()
        val reservationCalls = mutableListOf<ReservationCall>()

        override suspend fun getBookingInfo(parkingId: Int): BookingConfirmationModel {
            bookingInfoRequests += parkingId
            return info
        }

        override suspend fun makeReservation(
            parkingId: Int,
            driverId: Int,
            duration: Int,
            paymentMethod: String,
            clientName: String,
            vehiclePlate: String,
            vehicleType: String
        ): Int? {
            reservationCalls += ReservationCall(
                parkingId,
                driverId,
                duration,
                paymentMethod,
                clientName,
                vehiclePlate,
                vehicleType
            )
            return reservationId
        }

        override suspend fun observeBookingRealtime(bookingId: String): Flow<ReservationModel?> = emptyFlow()
    }

    private class FakeVehicleRepository(
        private val vehicle: VehicleModel?
    ) : RegisterVehicleRepository {
        override suspend fun completeDriverRegistration(
            user: UserModel,
            vehicle: VehicleModel
        ): Int? = vehicle.id

        override suspend fun getVehicleByDriverId(driverId: Int): VehicleModel? = vehicle
    }
}

