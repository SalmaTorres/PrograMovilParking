package com.easypark.app.earnings.presentation.viewmodel

import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.earnings.domain.model.EarningsSummary

data class EarningsUiState(
    val isLoading: Boolean = true,
    val summary: EarningsSummary? = null,
    val transactions: List<EarningTransaction> = emptyList(),
    val errorMessage: String? = null
)
