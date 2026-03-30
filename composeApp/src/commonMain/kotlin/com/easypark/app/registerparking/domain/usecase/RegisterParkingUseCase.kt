package com.easypark.app.registerparking.domain.usecase

import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.registerparking.domain.repository.ParkingRepository

class RegisterParkingUseCase(private val repository: ParkingRepository) {
    suspend fun invoke(model: ParkingModel): Result<Unit> {
        return repository.registerParking(model)
    }
}