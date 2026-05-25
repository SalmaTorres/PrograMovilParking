package com.easypark.app.spacemanagement.presentation.viewmodel

import com.easypark.app.core.FakeGetReservationSummaryUseCase
import com.easypark.app.core.FakeSpaceManagementRepository
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementEffect
import com.easypark.app.spacemanagement.presentation.state.SpaceManagementUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
class SpaceManagementViewModelTest {

    private val repository = FakeSpaceManagementRepository()
    private val evacuateUseCase = FakeGetReservationSummaryUseCase()
    private val sessionManager = SessionManager()

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenInit_automaticallyCallsEvacuate() = runTest {
        // Arrange
        sessionManager.currentParkingId = 1

        // Act
        val viewModel = SpaceManagementViewModel(repository, sessionManager, evacuateUseCase)
        advanceUntilIdle()

        // Assert
        assertTrue(evacuateUseCase.evacuateCalled)
    }

    @Test
    fun whenObserveParkingSpotsEmits_updatesSpotsStateAndCalculatesSummaryCorrectly() = runTest {
        // Arrange
        sessionManager.currentParkingId = 1
        val viewModel = SpaceManagementViewModel(repository, sessionManager, evacuateUseCase)

        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertEquals(emptyList(), initialState.parkingSpots)
            assertEquals(null, initialState.summary)

            // Act
            val liveSpots = listOf(
                ParkingSpot(id = 1, number = 101, isOccupied = true),
                ParkingSpot(id = 2, number = 102, isOccupied = false),
                ParkingSpot(id = 3, number = 103, isOccupied = true),
                ParkingSpot(id = 4, number = 104, isOccupied = false)
            )
            repository.parkingSpotsFlow.emit(liveSpots)

            // Act/Assert (Skip intermediate loading state if emitted)
            var finalState = awaitItem()
            if (finalState.isLoading) {
                finalState = awaitItem()
            }

            assertEquals(liveSpots, finalState.parkingSpots)
            assertEquals(SpaceSummary(totalCapacity = 4, occupied = 2, available = 2), finalState.summary)
            assertEquals(false, finalState.isLoading)
        }
    }

    @Test
    fun whenOccupySpotCalled_invokesRepositoryOccupyParkingSpot() = runTest {
        // Arrange
        sessionManager.currentParkingId = 1
        val viewModel = SpaceManagementViewModel(repository, sessionManager, evacuateUseCase)
        advanceUntilIdle()

        // Act
        viewModel.occupySpot(5)
        advanceUntilIdle()

        // Assert
        assertTrue(repository.occupyParkingSpotCalled)
        assertEquals(1, repository.occupyParkingId)
        assertEquals(5, repository.occupySpaceId)
    }

    @Test
    fun whenReleaseSpotCalled_invokesRepositoryReleaseParkingSpot() = runTest {
        // Arrange
        sessionManager.currentParkingId = 1
        val viewModel = SpaceManagementViewModel(repository, sessionManager, evacuateUseCase)
        advanceUntilIdle()

        // Act
        viewModel.releaseSpot(12)
        advanceUntilIdle()

        // Assert
        assertTrue(repository.releaseParkingSpotCalled)
        assertEquals(1, repository.releaseParkingId)
        assertEquals(12, repository.releaseSpaceId)
    }
}
