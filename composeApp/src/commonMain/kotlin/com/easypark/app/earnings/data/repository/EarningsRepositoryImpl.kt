package com.easypark.app.earnings.data.repository

import com.easypark.app.core.data.datasource.ReservationLocalDataSource
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.domain.model.EarningsSummaryModel
import com.easypark.app.earnings.domain.repository.EarningsRepository

class EarningsRepositoryImpl(
    private val reservationDS: ReservationLocalDataSource,
    private val spaceDS: SpaceLocalDataSource
) : EarningsRepository {
    override suspend fun getEarningsSummary(parkingId: Int): EarningsSummaryModel {
        val reservations = reservationDS.readByParking(parkingId)
        val total = reservations.sumOf { it.totalPrice }

        val totalSpaces = spaceDS.countTotal(parkingId)
        val available = spaceDS.countAvailable(parkingId)
        val occupied = totalSpaces - available

        return EarningsSummaryModel(
            totalEarnings = total,
            percentageChange = 12.5, // Dato simulado o calculado
            activeReservations = reservations.count { it.state == "ACTIVE" },
            reservationChange = 2, // Dato simulado
            occupiedSpaces = occupied,
            totalSpaces = totalSpaces,
            isCapacityLimited = (occupied.toDouble() / totalSpaces) > 0.8
        )
    }

    override suspend fun getEarningsHistory(parkingId: Int): List<EarningTransactionModel> {
        val reservations = reservationDS.readByParking(parkingId)

        val spaces = spaceDS.getMySpaces(parkingId)
        val spaceMap = spaces.associate { it.id to it.number }

        return reservations.map { res ->
            EarningTransactionModel(
                id = res.id,
                date = "HOY",
                label = "Espacio ${spaceMap[res.spaceId] ?: "?"}",
                amount = res.totalPrice
            )
        }
    }
    override suspend fun getTotalEarnings(parkingId: Int): Double {
        val reservations = reservationDS.readByParking(parkingId)
        return reservations
            .filter { it.state == "ACTIVE" || it.state == "PAID" }
            .sumOf { it.totalPrice }
    }
}