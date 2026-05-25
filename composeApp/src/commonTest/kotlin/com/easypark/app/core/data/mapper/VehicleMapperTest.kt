package com.easypark.app.core.data.mapper

import com.easypark.app.registervehicle.data.dto.VehicleDTO
import com.easypark.app.registervehicle.domain.model.VehicleModel
import kotlin.test.Test
import kotlin.test.assertEquals

class VehicleMapperTest {
    @Test
    fun vehicleRoundTripPreservesLocalFields() {
        val source = VehicleModel(3, 8, "ABC-123", "Automovil", "Corolla", "Azul")

        val result = source.toEntity(driverId = 8).apply { id = source.id }.toModel()

        assertEquals(source.id, result.id)
        assertEquals(source.driverId, result.driverId)
        assertEquals(source.plate, result.plate)
        assertEquals(source.type, result.type)
        assertEquals(source.model, result.model)
        assertEquals(source.color, result.color)
    }

    @Test
    fun remoteVehiclePreservesTypeWhenUploaded() {
        val source = VehicleModel(3, 8, "ABC-123", "Camioneta", "Hilux", "Gris")

        val dto = source.toRemote(driverId = 8)

        assertEquals("Camioneta", dto.type)
        assertEquals("ABC-123", dto.plate)
    }

    @Test
    fun nullableRemoteVehicleUsesSafeDefaults() {
        val result = VehicleDTO(id = null, driverId = null, plate = null, type = null).toDomain()

        assertEquals(0, result.id)
        assertEquals("", result.plate)
        assertEquals(VehicleDTO().type, result.type)
    }
}
