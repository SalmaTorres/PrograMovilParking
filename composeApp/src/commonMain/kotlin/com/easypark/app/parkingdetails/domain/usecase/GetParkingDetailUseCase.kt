package com.easypark.app.parkingdetails.domain.usecase

import com.easypark.app.parkingdetails.domain.model.ParkingDetail
import com.easypark.app.core.domain.repository.ParkingRepository

class GetParkingDetailUseCase(
    val repository: ParkingRepository
) {
    suspend operator fun invoke(id: String): ParkingDetail {
        return repository.getParkingDetail(id)
    }
}
