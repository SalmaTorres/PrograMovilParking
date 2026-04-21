package com.easypark.app.spacemanagement.domain.usecase

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository

class GetSpaceDataUseCase(private val repository: SpaceManagementRepository) {
    suspend fun getSummary(parkingId: Int): SpaceSummary {
        return repository.getSpaceSummary(parkingId)
    }

    suspend fun getSpots(parkingId: Int): List<ParkingSpot> {
        return repository.getParkingSpots(parkingId)
    }
}