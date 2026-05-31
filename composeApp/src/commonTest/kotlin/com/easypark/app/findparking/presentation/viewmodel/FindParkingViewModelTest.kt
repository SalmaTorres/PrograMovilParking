package com.easypark.app.findparking.presentation.viewmodel

import ReservationModel
import app.cash.turbine.test
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.findparking.domain.repository.FindParkingRepository
import com.easypark.app.findparking.presentation.state.FindParkingEffect
import com.easypark.app.findparking.presentation.state.FindParkingEvent
import com.easypark.app.registerparking.domain.model.ParkingModel
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FindParkingViewModelTest {
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
    fun initializationLoadsRealtimeParkingsAndRunsEvacuation() = runTest {
        val repository = FakeFindParkingRepository(parkings())
        val evacuationRepository = FakeReservationSummaryRepository()
        val viewModel = viewModel(repository, evacuationRepository)

        advanceUntilIdle()

        assertEquals(parkings(), viewModel.state.value.allParkings)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(1, repository.observeCalls)
        assertEquals(1, evacuationRepository.evacuateCalls)
    }

    @Test
    fun queryFiltersSuggestionsByNameIgnoringCase() = runTest {
        val viewModel = viewModel(FakeFindParkingRepository(parkings()))
        advanceUntilIdle()

        viewModel.onEvent(FindParkingEvent.OnQueryChanged("sur"))
        advanceUntilIdle()

        assertEquals("sur", viewModel.state.value.searchQuery)
        assertEquals(listOf(parkings()[1]), viewModel.state.value.suggestions)
    }

    @Test
    fun emptyQueryClearsSuggestionsAndSelectedParking() = runTest {
        val viewModel = viewModel(FakeFindParkingRepository(parkings()))
        advanceUntilIdle()

        viewModel.onEvent(FindParkingEvent.OnMarkerClicked(parkings()[0]))
        viewModel.onEvent(FindParkingEvent.OnQueryChanged(""))
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.searchQuery)
        assertEquals(emptyList(), viewModel.state.value.suggestions)
        assertEquals(null, viewModel.state.value.selectedParking)
    }

    @Test
    fun selectingSuggestionMovesCameraAndStoresSelection() = runTest {
        val selected = parkings()[0]
        val viewModel = viewModel(FakeFindParkingRepository(parkings()))
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(FindParkingEvent.OnSuggestionSelected(selected))
            advanceUntilIdle()

            assertEquals(selected, viewModel.state.value.selectedParking)
            assertEquals(FindParkingEffect.MoveCamera(selected.latitude, selected.longitude), awaitItem())
        }
    }

    @Test
    fun reserveAndDetailsClicksNavigateWithSelectedParkingId() = runTest {
        val selected = parkings()[0]
        val viewModel = viewModel(FakeFindParkingRepository(parkings()))
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(FindParkingEvent.OnMarkerClicked(selected))
            viewModel.onEvent(FindParkingEvent.OnReserveClick)
            viewModel.onEvent(FindParkingEvent.OnDetailsClick)
            advanceUntilIdle()

            assertEquals(FindParkingEffect.NavigateToBooking(selected.id), awaitItem())
            assertEquals(FindParkingEffect.NavigateToDetails(selected.id), awaitItem())
        }
    }

    private fun viewModel(
        repository: FakeFindParkingRepository,
        evacuationRepository: FakeReservationSummaryRepository = FakeReservationSummaryRepository()
    ) = FindParkingViewModel(
        repository = repository,
        evacuateUseCase = GetReservationSummaryUseCase(evacuationRepository),
        sessionManager = SessionManager()
    )

    private fun parkings() = listOf(
        ParkingModel(
            id = 1,
            ownerId = 10,
            name = "Parqueo Centro",
            address = "Av. Heroínas",
            latitude = -17.392,
            longitude = -66.157,
            pricePerHour = PriceModel(10.0),
            totalSpaces = 8,
            availableSpaces = 2
        ),
        ParkingModel(
            id = 2,
            ownerId = 11,
            name = "Garaje Sur",
            address = "Av. Panamericana",
            latitude = -17.401,
            longitude = -66.165,
            pricePerHour = PriceModel(8.0),
            totalSpaces = 5,
            availableSpaces = 1
        )
    )

    private class FakeFindParkingRepository(
        private val liveParkings: List<ParkingModel>
    ) : FindParkingRepository {
        var observeCalls = 0

        override suspend fun getAvailableParkings(): List<ParkingModel> = liveParkings

        override fun observeParkingsRealtime(): Flow<List<ParkingModel>> {
            observeCalls += 1
            return flowOf(liveParkings)
        }
    }

    private class FakeReservationSummaryRepository : ReservationSummaryRepository {
        var evacuateCalls = 0

        override suspend fun getActiveReservations(userId: Int): List<ReservationModel> = emptyList()

        override fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>> = flowOf(emptyList())

        override suspend fun checkInReservation(reservationId: Int, parkingId: Int, spaceId: Int) = Unit

        override suspend fun checkOutReservation(reservationId: Int, parkingId: Int, spaceId: Int) = Unit

        override suspend fun checkAndEvacuateExpiredReservations() {
            evacuateCalls += 1
        }

        override suspend fun checkAndCancelReservation(reservationId: Int, parkingId: Int, spaceId: Int) = Unit
    }
}

