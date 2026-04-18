package com.easypark.app.registerparking.domain.usecase

import com.easypark.app.core.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.repository.ParkingRepository

class RegisterParkingUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(user: UserModel, parking: ParkingModel): Boolean {
        return repository.completeOwnerRegistration(user, parking)
    }
}