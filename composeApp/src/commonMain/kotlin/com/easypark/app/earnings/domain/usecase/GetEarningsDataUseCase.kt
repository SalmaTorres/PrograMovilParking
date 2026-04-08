package com.easypark.app.earnings.domain.usecase

import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.earnings.domain.model.EarningsSummary
import com.easypark.app.earnings.domain.repository.EarningsRepository

data class EarningsData(
    val summary: EarningsSummary,
    val history: List<EarningTransaction>
)

class GetEarningsDataUseCase(
    private val repository: EarningsRepository
) {
    suspend fun execute(): EarningsData {
        val summary = repository.getEarningsSummary()
        val history = repository.getEarningsHistory()
        return EarningsData(summary, history)
    }
}
