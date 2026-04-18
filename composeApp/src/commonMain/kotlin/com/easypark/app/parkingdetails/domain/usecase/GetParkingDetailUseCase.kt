package com.easypark.app.parkingdetails.domain.usecase

import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.repository.ParkingRepository

class GetParkingDetailUseCase(
    val repository: ParkingRepository
) {
    suspend operator fun invoke(id: Int): ParkingModel {
        return repository.getParkingDetail(id)
    }
}
