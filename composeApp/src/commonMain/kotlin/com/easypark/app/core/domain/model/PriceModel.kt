package com.easypark.app.core.domain.model

import com.easypark.app.core.domain.model.status.Currency
import kotlinx.serialization.Serializable

@Serializable
data class PriceModel(
    val amount: Double,
    val currency: Currency = Currency.BOB
) {
    fun format(): String {
        val integerPart = amount.toInt()
        val decimalPart = ((amount - integerPart) * 100).toInt()
        val decimalString = if (decimalPart < 10) "0$decimalPart" else "$decimalPart"

        return "$integerPart.$decimalString ${currency.symbol}"
    }
}