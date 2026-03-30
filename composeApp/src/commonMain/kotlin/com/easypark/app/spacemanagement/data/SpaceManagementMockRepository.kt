package com.easypark.app.spacemanagement.data

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import kotlinx.coroutines.delay

class SpaceManagementMockRepository : SpaceManagementRepository {
    override suspend fun getSpaceSummary(): SpaceSummary {
        delay(500) // Simular retardo de red
        return SpaceSummary(
            totalCapacity = 50,
            occupied = 32,
            available = 18
        )
    }

    override suspend fun getParkingSpots(): List<ParkingSpot> {
        delay(500) // Simular retardo de red
        return listOf(
            ParkingSpot("1", "A-01", false),
            ParkingSpot("2", "A-01", false),
            ParkingSpot("3", "A-01", false),
            ParkingSpot("4", "A-02", true),
            ParkingSpot("5", "A-03", false),
            ParkingSpot("6", "B-02", false),
            ParkingSpot("7", "B-03", true),
            ParkingSpot("8", "B-03", true),
            ParkingSpot("9", "B-03", true),
            ParkingSpot("10", "C-01", true),
            ParkingSpot("11", "C-02", false),
            ParkingSpot("12", "C-02", false)
        )
    }
}
