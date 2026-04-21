package com.easypark.app.findparking.data.repository

import com.easypark.app.core.data.datasource.SpaceLocalDataSource
import com.easypark.app.core.data.mapper.toModel
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.findparking.data.datasource.FindParkingLocalDataSource
import com.easypark.app.findparking.domain.repository.FindParkingRepository
import com.easypark.app.parkingdetails.data.datasource.ParkingDetailsLocalDataSource

class FindParkingRepositoryImpl(
    private val parkingDS: FindParkingLocalDataSource,
    private val spaceDS: SpaceLocalDataSource,
    private val reviewDS: ParkingDetailsLocalDataSource
) : FindParkingRepository {

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