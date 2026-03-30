package com.easypark.app.parkingdetails.data.repository

import com.easypark.app.parkingdetails.domain.model.ParkingDetail
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailRepository
import kotlinx.coroutines.delay

class FakeParkingDetailRepositoryImpl : ParkingDetailRepository {
    override suspend fun getParkingDetail(id: String): ParkingDetail {
        delay(500)
        return ParkingDetail(
            id = id,
            name = "Estacionamiento Central",
            rating = 4.5,
            reviewCount = 120,
            address = "Av. Principal 123, Ciudad",
            pricePerHour = "$ 2.50",
            schedule = "08:00 - 22:00",
            isAvailable = true,
            imageUrl = null
        )
    }
}
