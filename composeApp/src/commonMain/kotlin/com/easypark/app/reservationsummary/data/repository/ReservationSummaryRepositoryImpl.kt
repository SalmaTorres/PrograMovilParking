package com.easypark.app.reservationsummary.data.repository

import ReservationModel
import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ReservationSummaryRepositoryImpl(
    private val localDS: ReservationSummaryLocalDataSource,
    private val firebaseManager: FirebaseManager
) : ReservationSummaryRepository {

    override fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>> {
        return firebaseManager.observeData("reservations").map { json ->
            if (json == null) return@map emptyList<ReservationModel>()

            try {
                val allReservations = Json.decodeFromString<Map<String, ReservationDTO>>(json)

                allReservations.values
                    .filter { it.status == "ACTIVE" }
                    .map { dto ->
                        dto.toDomain()
                    }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getActiveReservations(userId: Int): List<ReservationModel> {
        println("DEBUG-REPO: Buscando reservas para el usuario ID: $userId")

        val entities = localDS.getReservationsByUser(userId)

        println("DEBUG-REPO: Reservas encontradas en DB: ${entities.size}")

        val activeEntities = entities.filter { it.state == "ACTIVE" }
        println("DEBUG-REPO: Reservas con estado ACTIVE: ${activeEntities.size}")

        return activeEntities.mapNotNull { res ->
            val space = localDS.getSpace(res.spaceId) ?: return@mapNotNull null
            val parking = localDS.getParking(space.parkingId) ?: return@mapNotNull null

            ReservationModel(
                id = res.id,
                parkingName = parking.name,
                address = parking.address,
                spaceId = res.spaceId,
                spaceNumber = space.number,
                startTime = res.startHour,
                endTime = res.finalHour,
                totalPrice = PriceModel(amount = res.totalPrice),
                paymentMethod = res.methodPay,
                status = res.state
            )
        }
    }
}