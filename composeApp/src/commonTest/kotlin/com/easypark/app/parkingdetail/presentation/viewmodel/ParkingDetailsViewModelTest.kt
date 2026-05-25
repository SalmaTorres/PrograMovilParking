package com.easypark.app.parkingdetails.presentation.viewmodel

import app.cash.turbine.test
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailsRepository
import com.easypark.app.parkingdetails.domain.usecase.GetParkingDetailUseCase
import com.easypark.app.parkingdetails.domain.usecase.RateParkingUseCase
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsEffect
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsEvent
import com.easypark.app.registerparking.domain.model.ParkingModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
class ParkingDetailsViewModelTest {
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
    fun initializationObservesAndDisplaysParkingDetails() = runTest {
        val repository = FakeParkingDetailsRepository(detail())
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(detail(), viewModel.state.value.parkingDetail)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(1, repository.observeCalls)
    }

    @Test
    fun reserveClickNavigatesWithLoadedParkingId() = runTest {
        val viewModel = viewModel(FakeParkingDetailsRepository(detail()))
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(ParkingDetailsEvent.OnReserveClick)
            advanceUntilIdle()

            assertEquals(ParkingDetailsEffect.NavigateToBooking(10), awaitItem())
        }
    }

    @Test
    fun signedInUserCanRateWithoutStartingAnotherRealtimeObserver() = runTest {
        val repository = FakeParkingDetailsRepository(detail())
        val session = SessionManager().apply {
            saveSession(UserModel(6, "Melany", UserType.DRIVER, "m@e.com", "secret1", 70000000))
        }
        val viewModel = viewModel(repository, session)
        advanceUntilIdle()

        viewModel.onEvent(ParkingDetailsEvent.OnRate(5))
        advanceUntilIdle()

        assertEquals(5, viewModel.state.value.userRating)
        assertEquals(listOf(RatingCall(10, 6, 5f)), repository.ratings)
        assertEquals(1, repository.observeCalls)
    }

    @Test
    fun anonymousUserCannotRateAndReceivesAnErrorEffect() = runTest {
        val repository = FakeParkingDetailsRepository(detail())
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(ParkingDetailsEvent.OnRate(4))
            advanceUntilIdle()

            assertIs<ParkingDetailsEffect.ShowError>(awaitItem())
            assertEquals(emptyList(), repository.ratings)
        }
    }

    private fun viewModel(
        repository: FakeParkingDetailsRepository,
        session: SessionManager = SessionManager()
    ) = ParkingDetailsViewModel(
        parkingId = 10,
        getParkingDetailUseCase = GetParkingDetailUseCase(repository),
        rateParkingUseCase = RateParkingUseCase(repository),
        sessionManager = session
    )

    private fun detail() = ParkingModel(
        id = 10,
        ownerId = 2,
        name = "Central",
        address = "Calle 1",
        latitude = -17.3,
        longitude = -66.1,
        pricePerHour = PriceModel(7.5),
        rating = 4f,
        totalSpaces = 8,
        availableSpaces = 3
    )

    private data class RatingCall(val parkingId: Int, val userId: Int, val rating: Float)

    private class FakeParkingDetailsRepository(
        private val liveDetail: ParkingModel?
    ) : ParkingDetailsRepository {
        val ratings = mutableListOf<RatingCall>()
        var observeCalls = 0

        override suspend fun getParkingDetail(id: Int): ParkingModel =
            requireNotNull(liveDetail)

        override suspend fun rateParking(parkingId: Int, userId: Int, rating: Float) {
            ratings += RatingCall(parkingId, userId, rating)
        }

        override fun observeParkingDetail(id: Int): Flow<ParkingModel?> {
            observeCalls += 1
            return flowOf(liveDetail)
        }
    }
}

