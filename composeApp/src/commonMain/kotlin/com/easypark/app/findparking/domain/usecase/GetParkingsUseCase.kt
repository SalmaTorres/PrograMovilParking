package com.easypark.app.findparking.domain.usecase

import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.findparking.domain.repository.FindParkingRepository

class GetParkingsUseCase(private val repository: FindParkingRepository) {
    suspend operator fun invoke(): List<ParkingModel> {
        return repository.getAvailableParkings()
    }
}