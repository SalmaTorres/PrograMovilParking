package com.easypark.app.earnings.domain.repository

import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.earnings.domain.model.EarningsSummary
import kotlinx.coroutines.flow.Flow

interface EarningsRepository {
    fun getEarningsSummary(): Flow<EarningsSummary>
    fun getEarningsHistory(): Flow<List<EarningTransaction>>
}
