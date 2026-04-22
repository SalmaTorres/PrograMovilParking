package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.PriceDTO
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.status.Currency

fun PriceDTO.toDomain() = PriceModel(
    amount = amount ?: 0.0,
    currency = Currency.BOB
)