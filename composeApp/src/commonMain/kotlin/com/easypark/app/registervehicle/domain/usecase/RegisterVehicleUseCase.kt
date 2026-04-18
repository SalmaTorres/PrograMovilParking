package com.easypark.app.registervehicle.domain.usecase

import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.VehicleModel
import com.easypark.app.core.domain.repository.VehicleRepository

class RegisterVehicleUseCase(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(user: UserModel, vehicle: VehicleModel): Boolean {
        return repository.completeDriverRegistration(user, vehicle)
    }
}