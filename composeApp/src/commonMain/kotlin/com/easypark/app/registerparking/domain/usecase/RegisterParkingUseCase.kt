package com.easypark.app.registerparking.domain.usecase

import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registerparking.domain.repository.RegisterParkingRepository

class RegisterParkingUseCase(private val repository: RegisterParkingRepository) {
    suspend operator fun invoke(user: UserModel, parking: ParkingModel): Boolean {
        return repository.completeOwnerRegistration(user, parking)
    }
}