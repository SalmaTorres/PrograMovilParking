package com.easypark.app.spacemanagement.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository

class SpaceManagementRepositoryImpl(
    private val spaceDS: SpaceLocalDataSource
) : SpaceManagementRepository {

    override suspend fun getSpaceSummary(parkingId: Int): SpaceSummary {
        val total = spaceDS.countTotal(parkingId)
        val available = spaceDS.countAvailable(parkingId)
        val occupied = total - available

        return SpaceSummary(
            totalCapacity = total,
            occupied = occupied,
            available = available
        )
    }

    override suspend fun getParkingSpots(parkingId: Int): List<ParkingSpot> {
        val entities = spaceDS.getMySpaces(parkingId)

        return entities.map { entity ->
            ParkingSpot(
                id = entity.id,
                number = entity.number,
                isOccupied = entity.state == "OCUPADO"
            )
        }
    }
}