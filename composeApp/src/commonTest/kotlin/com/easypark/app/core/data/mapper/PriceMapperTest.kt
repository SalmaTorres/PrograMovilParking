package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.PriceDTO
import com.easypark.app.core.domain.model.status.Currency
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceMapperTest {
    @Test
    fun remotePriceMapsAmountInBob() {
        val result = PriceDTO(amount = 12.5, currency = "BOB").toDomain()

        assertEquals(12.5, result.amount)
        assertEquals(Currency.BOB, result.currency)
    }

    @Test
    fun nullRemoteAmountUsesZero() {
        val result = PriceDTO(amount = null, currency = null).toDomain()

        assertEquals(0.0, result.amount)
        assertEquals(Currency.BOB, result.currency)
    }
}
