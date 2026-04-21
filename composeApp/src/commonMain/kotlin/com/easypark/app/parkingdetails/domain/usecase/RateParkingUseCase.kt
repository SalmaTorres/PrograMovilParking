package com.easypark.app.parkingdetails.domain.usecase

import com.easypark.app.parkingdetails.domain.repository.ParkingDetailsRepository

class RateParkingUseCase(private val repository: ParkingDetailsRepository) {
    suspend operator fun invoke(parkingId: Int, userId: Int, rating: Float) {
        repository.rateParking(parkingId, userId, rating)
    }
}