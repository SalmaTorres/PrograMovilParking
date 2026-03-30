package com.easypark.app.spacemanagement.domain.usecase

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository

class GetSpaceDataUseCase(private val repository: SpaceManagementRepository) {
    suspend fun getSummary(): SpaceSummary {
        return repository.getSpaceSummary()
    }

    suspend fun getSpots(): List<ParkingSpot> {
        return repository.getParkingSpots()
    }
}
