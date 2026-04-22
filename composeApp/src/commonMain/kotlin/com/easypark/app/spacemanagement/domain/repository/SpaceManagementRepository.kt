package com.easypark.app.spacemanagement.domain.repository

import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import kotlinx.coroutines.flow.Flow

interface SpaceManagementRepository {
    suspend fun getSpaceSummary(parkingId: Int): SpaceSummary
    suspend fun getParkingSpots(parkingId: Int): List<ParkingSpot>
    fun observeSpaceSummary(parkingId: Int): Flow<SpaceSummary>
    fun observeParkingSpots(parkingId: Int): Flow<List<ParkingSpot>>
}