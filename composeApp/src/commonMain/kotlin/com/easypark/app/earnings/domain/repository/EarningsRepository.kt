package com.easypark.app.earnings.domain.repository

import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.domain.model.EarningsSummaryModel

interface EarningsRepository {
    suspend fun getEarningsSummary(id: Int): EarningsSummaryModel
    suspend fun getEarningsHistory(id: Int): List<EarningTransactionModel>
    suspend fun getTotalEarnings(parkingId: Int): Double
}