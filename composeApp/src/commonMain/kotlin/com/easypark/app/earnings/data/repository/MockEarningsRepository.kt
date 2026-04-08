package com.easypark.app.earnings.data.repository

import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.earnings.domain.model.EarningsSummary
import com.easypark.app.earnings.domain.repository.EarningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockEarningsRepository : EarningsRepository {
    override fun getEarningsSummary(): Flow<EarningsSummary> {
        return flowOf(
            EarningsSummary(
                totalEarnings = 4250.00,
                percentageChange = 12.5,
                activeReservations = 18,
                reservationChange = 5.0,
                occupiedSpaces = 42,
                totalSpaces = 50
            )
        )
    }

    override fun getEarningsHistory(): Flow<List<EarningTransaction>> {
        return flowOf(
            listOf(
                EarningTransaction("1", "1 NOV, 12:30 PM", "Reserva A-05", 15.00),
                EarningTransaction("2", "1 NOV, 01:15 PM", "Reserva B-12", 20.00),
                EarningTransaction("3", "1 NOV, 02:00 PM", "Reserva A-08", 15.00),
                EarningTransaction("4", "2 NOV, 09:00 AM", "Reserva C-03", 25.00),
                EarningTransaction("5", "2 NOV, 10:30 AM", "Reserva A-05", 15.00),
                EarningTransaction("6", "2 NOV, 11:45 AM", "Reserva B-07", 20.00),
                EarningTransaction("7", "2 NOV, 01:30 PM", "Reserva D-01", 30.00),
                EarningTransaction("8", "2 NOV, 03:00 PM", "Reserva A-10", 15.00)
            )
        )
    }
}
