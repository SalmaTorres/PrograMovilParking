package com.easypark.app.earnings.domain.usecase

import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.domain.model.EarningsSummaryModel
import com.easypark.app.earnings.domain.repository.EarningsRepository

data class EarningsData(
    val summary: EarningsSummaryModel,
    val history: List<EarningTransactionModel>
)

class GetEarningsDataUseCase(private val repository: EarningsRepository) {
    suspend fun execute(parkingId: Int): EarningsData {
        val summary = repository.getEarningsSummary(parkingId)
        val history = repository.getEarningsHistory(parkingId)
        return EarningsData(summary, history)
    }
}