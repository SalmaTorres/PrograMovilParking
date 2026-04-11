package com.easypark.app.shared.domain.model

enum class Currency(val symbol: String) {
    BOB("Bs"),
    USD("$"),
    EUR("€")
}

data class Price(
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