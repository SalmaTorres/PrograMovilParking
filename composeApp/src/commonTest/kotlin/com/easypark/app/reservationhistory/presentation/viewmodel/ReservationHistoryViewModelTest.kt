package com.easypark.app.reservationhistory.presentation.viewmodel

import app.cash.turbine.test
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.reservationhistory.presentation.state.ReservationHistoryEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationHistoryViewModelTest {
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
    fun initialization_observesRealtimeReservations_updatesState() = runTest {
        val sessionManager = SessionManager().apply {
            saveSession(
                user = UserModel(id = 1, name = "Admin", type = UserType.OWNER, email = "admin@test.com", password = "", cellphone = 12345),
                parkingId = 5
            )
        }
        val liveFlow = MutableSharedFlow<List<ReservationItemModel>>()
        val repository = FakeReservationHistoryRepository().apply {
            reservationsFlow = liveFlow
        }

        val viewModel = ReservationHistoryViewModel(sessionManager, repository)
        
        // Assert initial loading state
        assertTrue(viewModel.state.value.isLoading)

        // Run coroutines to trigger observeReservationsRealtime call
        advanceUntilIdle()

        assertEquals(5, repository.observeReservationsRealtimeParkingId)

        val sampleList = listOf(
            ReservationItemModel(1, "Carlos", "A-1", "10:00", "11:00", ReservationStatus.PENDIENTE),
            ReservationItemModel(2, "Maria", "A-2", "11:00", "12:00", ReservationStatus.FINISHED)
        )

        liveFlow.emit(sampleList)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(sampleList, viewModel.state.value.reservations)
    }

    @Test
    fun tabSelection_filtersReservationsCorrectly() = runTest {
        val sessionManager = SessionManager().apply {
            saveSession(
                user = UserModel(id = 1, name = "Admin", type = UserType.OWNER, email = "admin@test.com", password = "", cellphone = 12345),
                parkingId = 5
            )
        }
        val sampleReservations = listOf(
            ReservationItemModel(1, "Carlos", "A-1", "10:00", "11:00", ReservationStatus.PENDIENTE),
            ReservationItemModel(2, "Maria", "A-2", "11:00", "12:00", ReservationStatus.FINISHED),
            ReservationItemModel(3, "Juan", "A-3", "12:00", "13:00", ReservationStatus.OCUPADO),
            ReservationItemModel(4, "Ana", "A-4", "13:00", "14:00", ReservationStatus.CANCELADO),
            ReservationItemModel(5, "Luis", "A-5", "14:00", "15:00", ReservationStatus.ACTIVE),
            ReservationItemModel(6, "Elena", "A-6", "15:00", "16:00", ReservationStatus.ENDING_SOON)
        )
        val repository = FakeReservationHistoryRepository().apply {
            reservationsFlow = flowOf(sampleReservations)
        }

        val viewModel = ReservationHistoryViewModel(sessionManager, repository)
        advanceUntilIdle()

        // By default, selectedTab is 0 -> Active
        // Should show: PENDIENTE, OCUPADO, ACTIVE, ENDING_SOON
        val activeReservations = viewModel.state.value.filteredReservations
        assertEquals(4, activeReservations.size)
        assertTrue(activeReservations.any { it.id == 1 }) // PENDIENTE
        assertTrue(activeReservations.any { it.id == 3 }) // OCUPADO
        assertTrue(activeReservations.any { it.id == 5 }) // ACTIVE
        assertTrue(activeReservations.any { it.id == 6 }) // ENDING_SOON

        // Select Tab 1 -> History (FINISHED, CANCELADO)
        viewModel.onTabSelected(1)
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.selectedTab)
        val historyReservations = viewModel.state.value.filteredReservations
        assertEquals(2, historyReservations.size)
        assertTrue(historyReservations.any { it.id == 2 }) // FINISHED
        assertTrue(historyReservations.any { it.id == 4 }) // CANCELADO
    }

    @Test
    fun searchQueryChange_filtersClientNameCaseInsensitively() = runTest {
        val sessionManager = SessionManager().apply {
            saveSession(
                user = UserModel(id = 1, name = "Admin", type = UserType.OWNER, email = "admin@test.com", password = "", cellphone = 12345),
                parkingId = 5
            )
        }
        val sampleReservations = listOf(
            ReservationItemModel(1, "Carlos Gomez", "A-1", "10:00", "11:00", ReservationStatus.PENDIENTE),
            ReservationItemModel(2, "Maria Rodriguez", "A-2", "11:00", "12:00", ReservationStatus.PENDIENTE),
            ReservationItemModel(3, "carlos Sanchez", "A-3", "12:00", "13:00", ReservationStatus.PENDIENTE)
        )
        val repository = FakeReservationHistoryRepository().apply {
            reservationsFlow = flowOf(sampleReservations)
        }

        val viewModel = ReservationHistoryViewModel(sessionManager, repository)
        advanceUntilIdle()

        // Filter by query "cArLoS"
        viewModel.onQueryChanged("cArLoS")
        advanceUntilIdle()

        val filtered = viewModel.state.value.filteredReservations
        assertEquals(2, filtered.size)
        assertTrue(filtered.any { it.clientName == "Carlos Gomez" })
        assertTrue(filtered.any { it.clientName == "carlos Sanchez" })
    }

    @Test
    fun markArrivalEvent_callsRepositoryMarkArrival() = runTest {
        val sessionManager = SessionManager().apply {
            saveSession(
                user = UserModel(id = 1, name = "Admin", type = UserType.OWNER, email = "admin@test.com", password = "", cellphone = 12345),
                parkingId = 5
            )
        }
        val repository = FakeReservationHistoryRepository()
        val viewModel = ReservationHistoryViewModel(sessionManager, repository)
        advanceUntilIdle()

        viewModel.onEvent(ReservationHistoryEvent.OnCheckInClick(99))
        advanceUntilIdle()

        assertEquals(listOf(99), repository.markArrivalCalls)
    }

    private class FakeReservationHistoryRepository : ReservationHistoryRepository {
        var getReservationsUserId: Int? = null
        var observeReservationsRealtimeParkingId: Int? = null
        val markArrivalCalls = mutableListOf<Int>()

        var reservationsFlow: Flow<List<ReservationItemModel>> = flowOf(emptyList())

        override suspend fun getReservations(userId: Int): List<ReservationItemModel> {
            getReservationsUserId = userId
            return emptyList()
        }

        override fun observeReservationsRealtime(parkingId: Int): Flow<List<ReservationItemModel>> {
            observeReservationsRealtimeParkingId = parkingId
            return reservationsFlow
        }

        override suspend fun markArrival(reservationId: Int) {
            markArrivalCalls += reservationId
        }
    }
}
