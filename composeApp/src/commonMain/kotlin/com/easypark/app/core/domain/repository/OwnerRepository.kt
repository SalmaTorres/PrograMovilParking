package com.easypark.app.core.domain.repository

import com.easypark.app.core.domain.model.EarningsSummaryModel
import com.easypark.app.core.domain.model.IncomeTransactionModel
import com.easypark.app.core.domain.model.SpaceModel

interface OwnerRepository {
    suspend fun getEarningsSummary(): EarningsSummaryModel
    suspend fun getRecentIncome(): List<IncomeTransactionModel>

    suspend fun getParkingSpaces(parkingId: Int): List<SpaceModel>
    suspend fun updateSpaceAvailability(spaceId: Int, isOccupied: Boolean): Boolean
}