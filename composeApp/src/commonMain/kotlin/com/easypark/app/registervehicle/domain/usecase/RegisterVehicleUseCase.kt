package com.easypark.app.registervehicle.domain.usecase

import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository

class RegisterVehicleUseCase(private val repository: RegisterVehicleRepository) {
    suspend operator fun invoke(user: UserModel, vehicle: VehicleModel): Boolean {
        return repository.completeDriverRegistration(user, vehicle)
    }
}