package com.easypark.app.findparking.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.mapper.toDomain
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.findparking.data.datasource.FindParkingLocalDataSource
import com.easypark.app.findparking.domain.repository.FindParkingRepository
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource
import com.easypark.app.registerparking.data.dto.ParkingDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class FindParkingRepositoryImpl(
    private val parkingDS: FindParkingLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val reviewDS: ParkingDetailsLocalDataSource,
    private val firebaseManager: FirebaseManager
) : FindParkingRepository {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true // Si Firebase tiene campos extra, no crashea
        isLenient = true         // Permite formatos de texto más flexibles
        coerceInputValues = true // Si llega un null donde no debe, usa el valor por defecto
    }

    override fun observeParkingsRealtime(): Flow<List<ParkingModel>> {
        return firebaseManager.observeData("parkings").map { json ->
            if (json == null || json == "null" || json == "{}") return@map emptyList()
            try {
                // Intentamos decodificar usando la configuración tolerante
                val map = jsonConfig.decodeFromString<Map<String, ParkingDTO>>(json)
                map.values.map { it.toDomain() }
            } catch (e: Exception) {
                println("ERROR_MAPA: ${e.message}") // Mira esto en tu Logcat
                emptyList()
            }
        }
    }

    override suspend fun getAvailableParkings(): List<ParkingModel> {
        val entities = parkingDS.getAllParkings()

        return entities.map { entity ->
            val total = spaceDS.countTotal(entity.id)
            val available = spaceDS.countAvailable(entity.id)
            val reviewCount = reviewDS.countReviews(entity.id)

            entity.toModel(
                availableSpaces = available,
                reviewCount = reviewCount
            ).copy(totalSpaces = total)
        }
    }
}