package com.easypark.app.spacemanagement.domain.repository

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary

interface SpaceManagementRepository {
    suspend fun getSpaceSummary(): SpaceSummary
    suspend fun getParkingSpots(): List<ParkingSpot>
}
