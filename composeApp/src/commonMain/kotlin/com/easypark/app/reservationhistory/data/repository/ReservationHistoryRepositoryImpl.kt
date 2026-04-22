package com.easypark.app.reservationhistory.data.repository

import com.easypark.app.core.data.dto.ReservationDTO
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.reservationhistory.domain.repository.ReservationHistoryRepository
import com.easypark.app.reservationhistory.data.dao.ReservationHistoryDao
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ReservationHistoryRepositoryImpl(
    private val dao: ReservationHistoryDao,
    private val firebaseManager: FirebaseManager
) : ReservationHistoryRepository {

    override fun observeReservationsRealtime(parkingId: Int): Flow<List<ReservationItemModel>> {
         return firebaseManager.observeData("reservations").map { json ->
            if (json == null) return@map emptyList<ReservationItemModel>()

            try {
                val allReservations = Json.decodeFromString<Map<String, ReservationDTO>>(json)

                allReservations.values
                    .filter { it.id != null }
                    .map { dto ->
                        ReservationItemModel(
                            id = dto.id ?: 0,
                            clientName = "Usuario ${dto.id}",
                            spaceLabel = "Espacio ${dto.spaceNumber ?: "?"}",
                            startTime = "10:00 AM",
                            endTime = "12:00 PM",
                            status = if (dto.status == "ACTIVE") ReservationStatus.ACTIVE else ReservationStatus.FINISHED
                        )
                    }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getReservations(parkingId: Int): List<ReservationItemModel> {
        val entities = dao.getReservationsByParking(parkingId)

        return entities.map { res ->
            val user = dao.getUserById(res.driverId)
            val space = dao.getSpaceById(res.spaceId)

            ReservationItemModel(
                id = res.id,
                clientName = user?.name ?: "Cliente desconocido",
                spaceLabel = "Espacio ${space?.number ?: "?"}",
                startTime = "10:00 AM",
                endTime = "12:00 PM",
                status = if (res.state == "ACTIVE") ReservationStatus.ACTIVE else ReservationStatus.FINISHED
            )
        }
    }
}