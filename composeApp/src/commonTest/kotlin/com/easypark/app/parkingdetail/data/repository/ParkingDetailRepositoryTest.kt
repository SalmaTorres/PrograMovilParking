package com.easypark.app.parkingdetails.data.repository

import app.cash.turbine.test
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.entity.ParkingEntity
import com.easypark.app.core.data.entity.ReviewEntity
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ParkingDetailRepositoryTest {
    @Test
    fun localDetailIncludesLiveCapacityAndReviewCount() = runTest {
        val local = FakeParkingLocalDataSource(parkingEntity())
        local.reviews += review(rating = 5f)
        local.reviews += review(rating = 4f)
        val repository = repository(local, FakeSpaceDataSource(total = 8, available = 3))

        val detail = repository.getParkingDetail(10)

        assertEquals("Central", detail.name)
        assertEquals(8, detail.totalSpaces)
        assertEquals(3, detail.availableSpaces)
        assertEquals(2, detail.reviewCount)
        assertTrue(detail.isAvailable)
    }

    @Test
    fun realtimeDetailMapsFirebaseParkingResponse() = runTest {
        val paths = mutableListOf<String>()
        val repository = ParkingDetailRepositoryImpl(
            localDS = FakeParkingLocalDataSource(parkingEntity()),
            spaceDS = FakeSpaceDataSource(),
            observeRemoteData = { path ->
                paths += path
                flowOf(
                    """{"id":10,"ownerId":4,"name":"Central","address":"Calle 1","latitude":-17.3,"longitude":-66.1,"pricePerHour":{"amount":7.5,"currency":"BOB"},"rating":4.5,"totalSpaces":8,"availableSpaces":3,"reviewCount":6}"""
                )
            },
            saveRemoteData = { _, _ -> }
        )

        repository.observeParkingDetail(10).test {
            val detail = assertNotNull(awaitItem())
            assertEquals(10, detail.id)
            assertEquals(7.5, detail.pricePerHour.amount)
            assertEquals(6, detail.reviewCount)
            assertEquals(3, detail.availableSpaces)
            awaitComplete()
        }

        assertEquals(listOf("parkings/10"), paths)
    }

    @Test
    fun ratingPersistsReviewUpdatesAverageAndPublishesFirebaseValues() = runTest {
        val local = FakeParkingLocalDataSource(parkingEntity())
        local.reviews += review(rating = 2f)
        val writes = mutableListOf<Pair<String, String>>()
        val repository = ParkingDetailRepositoryImpl(
            localDS = local,
            spaceDS = FakeSpaceDataSource(),
            observeRemoteData = { flowOf(null) },
            saveRemoteData = { path, value -> writes += path to value }
        )

        repository.rateParking(parkingId = 10, userId = 7, rating = 4f)

        assertEquals(2, local.reviews.size)
        assertEquals(3f, local.updatedAverage)
        assertEquals("3.0", writes.first { it.first == "parkings/10/rating" }.second)
        assertEquals("2", writes.first { it.first == "parkings/10/reviewCount" }.second)
        assertTrue(writes.any { (path, value) ->
            path.startsWith("reviews/10/") && value.contains("\"userId\": 7") && value.contains("\"rating\": 4.0")
        })
    }

    private fun repository(
        local: FakeParkingLocalDataSource,
        spaces: FakeSpaceDataSource
    ) = ParkingDetailRepositoryImpl(
        localDS = local,
        spaceDS = spaces,
        observeRemoteData = { flowOf(null) },
        saveRemoteData = { _, _ -> }
    )

    private fun parkingEntity() = ParkingEntity(
        ownerId = 4,
        name = "Central",
        address = "Calle 1",
        latitude = -17.3,
        longitude = -66.1,
        pricePerHour = 7.5,
        rating = 4f,
        totalSpaces = 8
    ).apply { id = 10 }

    private fun review(rating: Float) = ReviewEntity(
        userId = 1,
        parkingId = 10,
        rating = rating
    )

    private class FakeParkingLocalDataSource(
        private val parking: ParkingEntity?
    ) : ParkingDetailsLocalDataSource {
        val reviews = mutableListOf<ReviewEntity>()
        var updatedAverage: Float? = null

        override suspend fun getById(id: Int): ParkingEntity? = parking

        override suspend fun saveReview(entity: ReviewEntity) {
            reviews += entity
        }

        override suspend fun getReviews(parkingId: Int): List<ReviewEntity> = reviews

        override suspend fun countReviews(parkingId: Int): Int = reviews.size

        override suspend fun updateAverageRating(parkingId: Int, newRating: Float) {
            updatedAverage = newRating
        }
    }

    private class FakeSpaceDataSource(
        private val total: Int = 0,
        private val available: Int = 0
    ) : SpaceLocalDataSource {
        override suspend fun countTotal(id: Int): Int = total
        override suspend fun countAvailable(id: Int): Int = available
        override suspend fun getById(spaceId: Int): SpaceEntity = SpaceEntity(0, 0)
        override suspend fun getMySpaces(parkingId: Int): List<SpaceEntity> = emptyList()
    }
}

