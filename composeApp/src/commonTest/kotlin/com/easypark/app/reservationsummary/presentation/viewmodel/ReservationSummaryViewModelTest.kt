package com.easypark.app.reservationsummary.presentation.viewmodel

import ReservationModel
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
import com.easypark.app.reservationsummary.domain.usecase.GetReservationSummaryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationSummaryViewModelTest {
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
    fun initializationObservesSignedInUserReservationsAndRunsEvacuation() = runTest {
        val expectedReservations = listOf(reservation())
        val repository = FakeReservationSummaryRepository(liveReservations = expectedReservations)
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(expectedReservations, viewModel.state.value.reservations)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(listOf(21), repository.observeRequests)
        assertEquals(1, repository.evacuateCalls)
    }

    @Test
    fun checkInAndCheckOutAreForwardedToUseCase() = runTest {
        val repository = FakeReservationSummaryRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.checkIn(reservationId = 5, parkingId = 3, spaceId = 2)
        viewModel.checkOut(reservationId = 5, parkingId = 3, spaceId = 2)
        advanceUntilIdle()

        assertEquals(listOf(ReservationAction(5, 3, 2)), repository.checkIns)
        assertEquals(listOf(ReservationAction(5, 3, 2)), repository.checkOuts)
    }

    @Test
    fun autoCancelReservationReleasesExpiredPendingReservation() = runTest {
        val repository = FakeReservationSummaryRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.autoCancelReservation(reservationId = 8, parkingId = 4, spaceId = 6)
        advanceUntilIdle()

        assertEquals(listOf(ReservationAction(8, 4, 6)), repository.cancellations)
    }

    @Test
    fun pendingReservationStillHasGraceTimeBeforeTenMinutes() {
        val nineMinutesAgo = Clock.System.now().toEpochMilliseconds() - (9 * 60 * 1000L)

        val remaining = reservation(startTime = nineMinutesAgo).getRemainingGracePeriodMillis()

        assertTrue(remaining in 1L..60_000L)
    }

    @Test
    fun pendingReservationHasNoGraceTimeAfterTenMinutes() {
        val elevenMinutesAgo = Clock.System.now().toEpochMilliseconds() - (11 * 60 * 1000L)

        val remaining = reservation(startTime = elevenMinutesAgo).getRemainingGracePeriodMillis()

        assertEquals(0L, remaining)
    }

    @Test
    fun remainingGraceTimeFormatsAsMinutesAndSeconds() {
        val result = reservation().formatRemainingTime(9 * 60 * 1000L + 5 * 1000L)

        assertEquals("09:05", result)
    }

    private fun viewModel(repository: FakeReservationSummaryRepository) = ReservationSummaryViewModel(
        sessionManager = signedInSession(),
        getReservationSummaryUseCase = GetReservationSummaryUseCase(repository)
    )

    private fun signedInSession() = SessionManager().apply {
        saveSession(UserModel(21, "Jhessa", UserType.DRIVER, "j@easypark.test", "secret", 70000000))
    }

    private fun reservation(
        id: Int = 5,
        startTime: Long = Clock.System.now().toEpochMilliseconds()
    ) = ReservationModel(
        id = id,
        parkingName = "Parqueo Centro",
        address = "Av. Heroínas",
        spaceId = 2,
        spaceNumber = 7,
        startTime = startTime,
        endTime = startTime + 60 * 60 * 1000L,
        totalPrice = PriceModel(10.0),
        status = "PENDIENTE",
        parkingId = 3
    )

    private data class ReservationAction(
        val reservationId: Int,
        val parkingId: Int,
        val spaceId: Int
    )

    private class FakeReservationSummaryRepository(
        private val liveReservations: List<ReservationModel> = emptyList()
    ) : ReservationSummaryRepository {
        val observeRequests = mutableListOf<Int>()
        val checkIns = mutableListOf<ReservationAction>()
        val checkOuts = mutableListOf<ReservationAction>()
        val cancellations = mutableListOf<ReservationAction>()
        var evacuateCalls = 0

        override suspend fun getActiveReservations(userId: Int): List<ReservationModel> = liveReservations

        override fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>> {
            observeRequests += userId
            return flowOf(liveReservations)
        }

        override suspend fun checkInReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
            checkIns += ReservationAction(reservationId, parkingId, spaceId)
        }

        override suspend fun checkOutReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
            checkOuts += ReservationAction(reservationId, parkingId, spaceId)
        }

        override suspend fun checkAndEvacuateExpiredReservations() {
            evacuateCalls += 1
        }

        override suspend fun checkAndCancelReservation(reservationId: Int, parkingId: Int, spaceId: Int) {
            cancellations += ReservationAction(reservationId, parkingId, spaceId)
        }
    }
}

