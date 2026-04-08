package com.easypark.app.earnings.domain.usecase

import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.earnings.domain.model.EarningsSummary
import com.easypark.app.earnings.domain.repository.EarningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class EarningsData(
    val summary: EarningsSummary,
    val history: List<EarningTransaction>
)

class GetEarningsDataUseCase(
    private val repository: EarningsRepository
) {
    operator fun invoke(): Flow<EarningsData> {
        return combine(
            repository.getEarningsSummary(),
            repository.getEarningsHistory()
        ) { summary, history ->
            EarningsData(summary, history)
        }
    }
}
