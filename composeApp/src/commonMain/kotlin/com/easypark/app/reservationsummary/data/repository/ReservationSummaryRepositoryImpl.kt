package com.easypark.app.reservationsummary.data.repository

import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.core.domain.model.ReservationModel
import com.easypark.app.reservationsummary.data.datasource.ReservationSummaryLocalDataSource
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
// IMPORTANTE: Asegúrate de que estos imports existan
import kotlin.collections.firstOrNull

class ReservationSummaryRepositoryImpl(
    private val localDS: ReservationSummaryLocalDataSource
) : ReservationSummaryRepository {

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