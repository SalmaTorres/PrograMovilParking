package com.easypark.app.spacemanagement.domain.repository

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary

interface SpaceManagementRepository {
    suspend fun getSpaceSummary(parkingId: Int): SpaceSummary
    suspend fun getParkingSpots(parkingId: Int): List<ParkingSpot>
}