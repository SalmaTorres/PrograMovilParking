package com.easypark.app.earnings.presentation.state

import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.domain.model.EarningsSummaryModel

data class EarningsUiState(
    val isLoading: Boolean = true,
    val summary: EarningsSummaryModel? = null,
    val transactions: List<EarningTransactionModel> = emptyList(),
    val errorMessage: String? = null
)