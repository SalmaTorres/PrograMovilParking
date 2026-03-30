package com.easypark.app.registerparking.data

import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.registerparking.domain.repository.ParkingRepository

class MockParkingRepository : ParkingRepository {
    override suspend fun registerParking(parking: ParkingModel): Boolean {
        println("Simulando guardado en BD: $parking")
        return true
    }
}