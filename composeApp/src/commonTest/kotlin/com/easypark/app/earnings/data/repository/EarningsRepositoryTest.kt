package com.easypark.app.earnings.data.repository

import app.cash.turbine.test
import com.easypark.app.core.data.datasource.ReservationLocalDataSource
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.entity.ReservationEntity
import com.easypark.app.core.data.entity.SpaceEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class EarningsRepositoryTest {

    @Test
    fun getTotalEarnings_sumsActiveAndPaidReservationsOnly() = runTest {
        val reservationDS = FakeReservationLocalDataSource().apply {
            reservations += ReservationEntity(id = 1, spaceId = 10, driverId = 1, totalPrice = 12.5, state = "ACTIVE", methodPay = "QR")
            reservations += ReservationEntity(id = 2, spaceId = 10, driverId = 2, totalPrice = 8.0, state = "PAID", methodPay = "CASH")
            reservations += ReservationEntity(id = 3, spaceId = 10, driverId = 3, totalPrice = 15.0, state = "FINISHED", methodPay = "QR")
            reservations += ReservationEntity(id = 4, spaceId = 10, driverId = 4, totalPrice = 20.0, state = "CANCELADO", methodPay = "CASH")
        }

        val repository = EarningsRepositoryImpl(
            reservationDS = reservationDS,
            spaceDS = FakeSpaceLocalDataSource(),
            observeRemoteData = { flowOf(null) }
        )

        // Sum of ACTIVE (12.5) + PAID (8.0) = 20.5
        val total = repository.getTotalEarnings(10)
        assertEquals(20.5, total)
    }

    @Test
    fun getEarningsSummary_calculatesCorrectValues() = runTest {
        val reservationDS = FakeReservationLocalDataSource().apply {
            reservations += ReservationEntity(id = 1, spaceId = 10, driverId = 1, totalPrice = 10.0, state = "ACTIVE", methodPay = "QR")
            reservations += ReservationEntity(id = 2, spaceId = 10, driverId = 2, totalPrice = 25.0, state = "FINISHED", methodPay = "CASH")
        }
        val spaceDS = FakeSpaceLocalDataSource(total = 10, available = 3)

        val repository = EarningsRepositoryImpl(
            reservationDS = reservationDS,
            spaceDS = spaceDS,
            observeRemoteData = { flowOf(null) }
        )

        val summary = repository.getEarningsSummary(10)
        
        // totalEarnings = 10.0 + 25.0 = 35.0
        assertEquals(35.0, summary.totalEarnings)
        // activeReservations = count of "ACTIVE" = 1
        assertEquals(1, summary.activeReservations)
        // occupiedSpaces = total (10) - available (3) = 7
        assertEquals(7, summary.occupiedSpaces)
        // totalSpaces = 10
        assertEquals(10, summary.totalSpaces)
        // (occupied.toDouble() / totalSpaces) = 7 / 10 = 0.7 which is <= 0.8 -> false
        assertFalse(summary.isCapacityLimited)
    }

    @Test
    fun getEarningsSummary_capacityIsLimited_whenOccupiedGreaterThanEightyPercent() = runTest {
        val reservationDS = FakeReservationLocalDataSource()
        // total = 10, available = 1 -> occupied = 9. 9/10 = 0.9 > 0.8 -> capacity limited should be true.
        val spaceDS = FakeSpaceLocalDataSource(total = 10, available = 1)

        val repository = EarningsRepositoryImpl(
            reservationDS = reservationDS,
            spaceDS = spaceDS,
            observeRemoteData = { flowOf(null) }
        )

        val summary = repository.getEarningsSummary(10)
        assertTrue(summary.isCapacityLimited)
    }

    @Test
    fun observeTransactionsRealtime_filtersFinishedAndSortsDescending() = runTest {
        val jsonArray = """
            [
                {
                    "id": 101,
                    "parkingId": 10,
                    "spaceNumber": 3,
                    "endTime": 1000,
                    "status": "FINISHED",
                    "totalPrice": {
                        "amount": 10.0,
                        "currency": "BOB"
                    }
                },
                {
                    "id": 102,
                    "parkingId": 10,
                    "spaceNumber": 4,
                    "endTime": 3000,
                    "status": "FINISHED",
                    "totalPrice": {
                        "amount": 12.0,
                        "currency": "BOB"
                    }
                },
                {
                    "id": 103,
                    "parkingId": 10,
                    "spaceNumber": 5,
                    "endTime": 2000,
                    "status": "ACTIVE",
                    "totalPrice": {
                        "amount": 15.0,
                        "currency": "BOB"
                    }
                },
                {
                    "id": 104,
                    "parkingId": 99,
                    "spaceNumber": 1,
                    "endTime": 4000,
                    "status": "FINISHED",
                    "totalPrice": {
                        "amount": 20.0,
                        "currency": "BOB"
                    }
                }
            ]
        """.trimIndent()

        val repository = EarningsRepositoryImpl(
            reservationDS = FakeReservationLocalDataSource(),
            spaceDS = FakeSpaceLocalDataSource(),
            observeRemoteData = { flowOf(jsonArray) }
        )

        repository.observeTransactionsRealtime(10).test {
            val transactions = awaitItem()
            // Should contain id 101 and 102 because status is FINISHED and parkingId is 10.
            // Sorted descending by endTime: id 102 (endTime 3000) first, then id 101 (endTime 1000).
            assertEquals(2, transactions.size)

            val first = transactions[0]
            assertEquals(102, first.id)
            assertEquals("Espacio 4", first.label)
            assertEquals(12.0, first.amount)

            val second = transactions[1]
            assertEquals(101, second.id)
            assertEquals("Espacio 3", second.label)
            assertEquals(10.0, second.amount)

            awaitComplete()
        }
    }

    private class FakeReservationLocalDataSource : ReservationLocalDataSource {
        val reservations = mutableListOf<ReservationEntity>()

        override suspend fun readByParking(parkingId: Int): List<ReservationEntity> {
            return reservations.filter { it.spaceId == parkingId || it.id > 0 }
        }
    }

    private class FakeSpaceLocalDataSource(
        var total: Int = 10,
        var available: Int = 5,
        val spacesList: List<SpaceEntity> = emptyList()
    ) : SpaceLocalDataSource {
        override suspend fun countTotal(id: Int): Int = total
        override suspend fun countAvailable(id: Int): Int = available
        override suspend fun getById(spaceId: Int): SpaceEntity = SpaceEntity(0, 0)
        override suspend fun getMySpaces(parkingId: Int): List<SpaceEntity> = spacesList
    }
}
