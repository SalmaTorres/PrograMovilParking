package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.PriceDTO
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.registerparking.data.dto.ParkingDTO
import com.easypark.app.registerparking.domain.model.ParkingModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParkingMapperTest {
    private val parking = ParkingModel(
        id = 5,
        ownerId = 10,
        name = "Central",
        address = "Av. America 123",
        latitude = -17.39,
        longitude = -66.15,
        pricePerHour = PriceModel(8.5),
        rating = 4.2f,
        totalSpaces = 12,
        schedule = "08:00 - 20:00",
        reviewCount = 3,
        availableSpaces = 2
    )

    @Test
    fun entityConversionRetainsParkingAndComputesAvailability() {
        val result = parking.toEntity(ownerId = parking.ownerId).toModel(
            availableSpaces = 2,
            reviewCount = 3
        )

        assertEquals(parking.id, result.id)
        assertEquals(parking.pricePerHour, result.pricePerHour)
        assertEquals(3, result.reviewCount)
        assertTrue(result.isAvailable)

        assertFalse(parking.toEntity(parking.ownerId).toModel(availableSpaces = 0).isAvailable)
    }

    @Test
    fun firebaseParkingMapsValuesAndDefaults() {
        val mapped = ParkingDTO(
            id = 7,
            ownerId = 3,
            name = "Norte",
            pricePerHour = PriceDTO(6.0),
            availableSpaces = 1,
            totalSpaces = 5
        ).toDomain()
        val empty = ParkingDTO(
            id = null,
            name = null,
            pricePerHour = null,
            availableSpaces = null,
            ownerId = null
        ).toDomain()

        assertEquals(7, mapped.id)
        assertEquals(6.0, mapped.pricePerHour.amount)
        assertTrue(mapped.isAvailable)
        assertEquals(0, empty.id)
        assertEquals(0.0, empty.pricePerHour.amount)
        assertFalse(empty.isAvailable)
    }

    @Test
    fun remoteRegistrationContainsAssignedIdsAndCapacity() {
        val remote = parking.toRemote(id = 90, ownerId = 91)

        assertEquals(90, remote.id)
        assertEquals(91, remote.ownerId)
        assertEquals(parking.totalSpaces, remote.availableSpaces)
        assertEquals(parking.pricePerHour.amount, remote.pricePerHour?.amount)
    }
}
