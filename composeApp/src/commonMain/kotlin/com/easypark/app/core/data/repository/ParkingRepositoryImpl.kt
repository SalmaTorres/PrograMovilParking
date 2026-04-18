package com.easypark.app.core.data.repository

import com.easypark.app.core.data.datasource.ParkingLocalDataSource
import com.easypark.app.core.data.datasource.ReviewLocalDataSource
import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.datasource.UserLocalDataSource
import com.easypark.app.core.data.entity.SpaceEntity
import com.easypark.app.core.data.mapper.toEntity
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.repository.ParkingRepository

class ParkingRepositoryImpl(
    private val userDS: UserLocalDataSource,
    private val parkingDS: ParkingLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val reviewDS: ReviewLocalDataSource
) : ParkingRepository {
    override suspend fun completeOwnerRegistration(user: UserModel, parking: ParkingModel): Boolean {
        return try {
            val userId = userDS.save(user.toEntity())

            val parkingEntity = parking.toEntity().copy(ownerId = userId)
            val parkingId = parkingDS.save(parkingEntity)

            val spacesToCreate = (1..parking.totalSpaces).map { i ->
                SpaceEntity(
                    parkingId = parkingId,
                    number = i,
                    state = "LIBRE"
                )
            }

            spaceDS.saveList(spacesToCreate)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getParkingDetail(id: Int): ParkingModel {
        val parkingEntity = parkingDS.readById(id) ?: throw Exception("Not found")

        val total = spaceDS.countTotal(id)
        val available = spaceDS.countAvailable(id)

        val isAvailable = available > 0

        return parkingEntity.toModel().copy(
            totalSpaces = total,
            isAvailable = isAvailable,
            reviewCount = reviewDS.countForParking(id)
        )
    }

    override suspend fun getAvailableParkings(): List<ParkingModel> {
        val entities = parkingDS.readAll()

        return entities.map { entity ->
            val availableCount = spaceDS.countAvailable(entity.id)

            val isAvailable = availableCount > 0

            entity.toModel().copy(
                isAvailable = isAvailable
            )
        }
    }
}