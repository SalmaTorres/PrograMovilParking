package com.easypark.app.earnings.data.mapper

import com.easypark.app.earnings.domain.model.EarningsSummaryModel
import com.easypark.app.registerparking.data.dto.ParkingDTO

fun ParkingDTO.toEarningsSummary() = EarningsSummaryModel(
    totalEarnings = this.totalEarnings ?: 0.0,
    activeReservations = this.activeReservations ?: 0,
    totalSpaces = this.totalSpaces ?: 0,
    occupiedSpaces = (this.totalSpaces ?: 0) - (this.availableSpaces ?: 0),
    percentageChange = 12.5,
    reservationChange = 2,
    isCapacityLimited = ((this.totalSpaces ?: 0) - (this.availableSpaces ?: 0)).toDouble() / (this.totalSpaces ?: 1) > 0.8
)