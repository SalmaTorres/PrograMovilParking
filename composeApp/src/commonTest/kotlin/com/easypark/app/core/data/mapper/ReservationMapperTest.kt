package com.easypark.app.core.data.mapper

import ReservationModel
import com.easypark.app.core.data.dto.PriceDTO
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.domain.model.PriceModel
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class ReservationMapperTest {
    @Test
    fun reservationRoundTripPreservesPersistedFields() {
        val source = ReservationModel(
            id = 1,
            parkingName = "Central",
            address = "Calle 1",
            spaceId = 4,
            spaceNumber = 2,
            startTime = 100L,
            endTime = 200L,
            totalPrice = PriceModel(15.0),
            paymentMethod = "QR",
            status = "OCUPADO",
            vehiclePlate = "ABC-123",
            vehicleType = "Automovil",
            arrivalTime = 110L,
            parkingId = 9
        )

        val result = source.toEntity(driverId = 20).copy(id = source.id)
            .toModel(source.parkingName, source.address, source.spaceNumber, source.parkingId)

        assertEquals(source, result)
    }

    @Test
    fun expiredActiveRemoteReservationBecomesFinished() {
        val pastEnd = Clock.System.now().toEpochMilliseconds() - 1_000L

        val result = ReservationDTO(
            id = 1,
            status = "OCUPADO",
            endTime = pastEnd,
            totalPrice = PriceDTO(10.0)
        ).toDomain()

        assertEquals("FINISHED", result.status)
    }

    @Test
    fun pendingReservationIsNotFinishedBeforeCheckIn() {
        val pastEnd = Clock.System.now().toEpochMilliseconds() - 1_000L

        val result = ReservationDTO(status = "PENDIENTE", endTime = pastEnd).toDomain()

        assertEquals("PENDIENTE", result.status)
    }
}
