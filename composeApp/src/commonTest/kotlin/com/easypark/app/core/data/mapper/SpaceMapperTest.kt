package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.SpaceDTO
import com.easypark.app.core.domain.model.SpaceModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpaceMapperTest {
    @Test
    fun spaceRoundTripPreservesLocalFields() {
        val source = SpaceModel(id = 4, parkingId = 2, number = 6, state = "OCUPADO")

        val result = source.toEntity().apply { id = source.id }.toModel()

        assertEquals(source.id, result.id)
        assertEquals(source.parkingId, result.parkingId)
        assertEquals(source.number, result.number)
        assertEquals(source.state, result.state)
    }

    @Test
    fun firebaseSpaceDefaultsToFreeAndMapsOccupancy() {
        val free = SpaceDTO(id = null, parkingId = null, number = null, state = null).toDomain()
        val reservedSpot = SpaceDTO(id = 1, number = 2, state = "RESERVADO").toParkingSpot()
        val freeSpot = SpaceDTO(id = 2, number = 3, state = "LIBRE").toParkingSpot()

        assertEquals("LIBRE", free.state)
        assertTrue(reservedSpot.isOccupied)
        assertFalse(freeSpot.isOccupied)
    }
}
