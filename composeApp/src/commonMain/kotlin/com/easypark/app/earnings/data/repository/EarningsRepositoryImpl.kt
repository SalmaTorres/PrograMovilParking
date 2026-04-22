package com.easypark.app.earnings.data.repository

import com.easypark.app.core.data.datasource.ReservationLocalDataSource
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.earnings.data.mapper.toEarningsSummary
import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.domain.model.EarningsSummaryModel
import com.easypark.app.earnings.domain.repository.EarningsRepository
import com.easypark.app.registerparking.data.dto.ParkingDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class EarningsRepositoryImpl(
    private val reservationDS: ReservationLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val firebaseManager: FirebaseManager
) : EarningsRepository {

    override suspend fun observeEarningsRealtime(parkingId: Int): Flow<EarningsSummaryModel?> {
        return firebaseManager.observeData("parkings/$parkingId").map { json ->
            if (json == null) return@map null

            val dto = Json.decodeFromString<ParkingDTO>(json)

            dto.toEarningsSummary()
        }
    }

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