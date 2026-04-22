package com.easypark.app.earnings.data.repository

import com.easypark.app.core.data.datasource.ReservationLocalDataSource
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.earnings.data.dto.EarningsSummaryDTO
import com.easypark.app.earnings.data.mapper.toDomain
import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.domain.model.EarningsSummaryModel
import com.easypark.app.earnings.domain.repository.EarningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class EarningsRepositoryImpl(
    private val reservationDS: ReservationLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val firebaseManager: FirebaseManager
) : EarningsRepository {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true // Si Firebase tiene campos extra, no crashea
        isLenient = true         // Permite formatos de texto más flexibles
        coerceInputValues = true // Si llega un null donde no debe, usa el valor por defecto
    }

    override suspend fun observeEarningsRealtime(parkingId: Int): Flow<EarningsSummaryModel?> {
        return firebaseManager.observeData("parkings/$parkingId/summary").map { json ->
            if (json == null || json == "null") return@map null
            try {
                jsonConfig.decodeFromString<EarningsSummaryDTO>(json).toDomain()
            } catch (e: Exception) {
                println("ERROR_EARNINGS: ${e.message}")
                null
            }
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
        val json = firebaseManager.observeData("reservations").firstOrNull() ?: return emptyList()

        val spaces = spaceDS.getMySpaces(parkingId)
        val spaceMap = spaces.associate { it.id to it.number }

        return try {
            val element = jsonConfig.parseToJsonElement(json)
            val reservationsDTOs = if (element is kotlinx.serialization.json.JsonObject) {
                jsonConfig.decodeFromJsonElement<Map<String, com.easypark.app.core.data.dto.ReservationDTO>>(element).values.toList()
            } else if (element is kotlinx.serialization.json.JsonArray) {
                jsonConfig.decodeFromJsonElement<List<com.easypark.app.core.data.dto.ReservationDTO?>>(element).filterNotNull()
            } else {
                emptyList()
            }

            reservationsDTOs
                .filter { spaceMap.containsKey(it.spaceId) }
                .map { res ->
                    EarningTransactionModel(
                        id = res.id ?: 0,
                        date = "HOY",
                        label = "Espacio ${spaceMap[res.spaceId] ?: "?"}",
                        amount = res.totalPrice?.amount ?: 0.0
                    )
                }.reversed()
        } catch (e: Exception) {
            println("ERROR_EARNINGS: ${e.message}")
            emptyList()
        }
    }
    override suspend fun getTotalEarnings(parkingId: Int): Double {
        val reservations = reservationDS.readByParking(parkingId)
        return reservations
            .filter { it.state == "ACTIVE" || it.state == "PAID" }
            .sumOf { it.totalPrice }
    }
}