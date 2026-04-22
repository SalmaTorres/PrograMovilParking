package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.PriceDTO
import com.easypark.app.core.domain.model.PriceModel

fun PriceDTO.toDomain() = PriceModel(
    amount = this.amount ?: 0.0
)