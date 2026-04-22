package com.easypark.app.parkingdetails.domain.usecase

import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailsRepository

class GetParkingDetailUseCase(
    val repository: ParkingDetailsRepository
) {
    suspend operator fun invoke(id: Int): ParkingModel {
        return repository.getParkingDetail(id)
    }
    fun observe(id: Int) = repository.observeParkingDetail(id)
}