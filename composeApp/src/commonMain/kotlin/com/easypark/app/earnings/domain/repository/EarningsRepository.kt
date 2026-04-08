package com.easypark.app.earnings.domain.repository

import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.earnings.domain.model.EarningsSummary

interface EarningsRepository {
    suspend fun getEarningsSummary(): EarningsSummary
    suspend fun getEarningsHistory(): List<EarningTransaction>
}
