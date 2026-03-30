package com.easypark.app.parkingdetails.domain.usecase

import com.easypark.app.parkingdetails.domain.model.ParkingDetail
import com.easypark.app.parkingdetails.domain.repository.ParkingDetailRepository

class GetParkingDetailUseCase(
    val repository: ParkingDetailRepository
) {
    suspend operator fun invoke(id: String): ParkingDetail {
        return repository.getParkingDetail(id)
    }
}
