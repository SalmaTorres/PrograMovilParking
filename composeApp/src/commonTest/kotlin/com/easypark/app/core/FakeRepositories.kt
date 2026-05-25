package com.easypark.app.core

import ReservationModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.register.domain.repository.RegisterRepository
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.repository.RegisterVehicleRepository
import com.easypark.app.reservationsummary.domain.repository.ReservationSummaryRepository
import com.easypark.app.reservationsummary.domain.usecase.GetReservationSummaryUseCase
import com.easypark.app.signin.domain.repository.AuthRepository
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow

class FakeAuthRepository : AuthRepository {
    var stubbedUser: UserModel? = null
    var stubbedParkingId: Int? = null

    var loginCalled = false
    var loginEmail: String? = null
    var loginPass: String? = null

    var getParkingIdCalled = false
    var getParkingIdOwnerId: Int? = null

    override suspend fun login(email: String, pass: String): UserModel? {
        loginCalled = true
        loginEmail = email
        loginPass = pass
        return stubbedUser
    }

    override suspend fun getParkingIdByOwner(ownerId: Int): Int? {
        getParkingIdCalled = true
        getParkingIdOwnerId = ownerId
        return stubbedParkingId
    }

    override suspend fun loginFromCloud(email: String, pass: String): UserModel? = null
}

class FakeRegisterRepository : RegisterRepository {
    var stubbedEmailAvailable = true
    var savedUser: UserModel? = null

    var isEmailAvailableCalled = false
    var isEmailAvailableEmail: String? = null

    var saveUserToCloudCalled = false

    override suspend fun isEmailAvailable(email: String): Boolean {
        isEmailAvailableCalled = true
        isEmailAvailableEmail = email
        return stubbedEmailAvailable
    }

    override suspend fun saveUserToCloud(user: UserModel) {
        saveUserToCloudCalled = true
        savedUser = user
    }
}

class FakeRegisterVehicleRepository : RegisterVehicleRepository {
    var stubbedRegisteredUserId: Int? = null
    var registeredUser: UserModel? = null
    var registeredVehicle: VehicleModel? = null

    var completeDriverRegistrationCalled = false

    override suspend fun completeDriverRegistration(user: UserModel, vehicle: VehicleModel): Int? {
        completeDriverRegistrationCalled = true
        registeredUser = user
        registeredVehicle = vehicle
        return stubbedRegisteredUserId
    }

    override suspend fun getVehicleByDriverId(driverId: Int): VehicleModel? = null
}

class FakeSpaceManagementRepository : SpaceManagementRepository {
    val parkingSpotsFlow = MutableSharedFlow<List<ParkingSpot>>(replay = 1)
    
    var occupyParkingSpotCalled = false
    var occupyParkingId: Int? = null
    var occupySpaceId: Int? = null

    var releaseParkingSpotCalled = false
    var releaseParkingId: Int? = null
    var releaseSpaceId: Int? = null

    override suspend fun getSpaceSummary(parkingId: Int): SpaceSummary = SpaceSummary(0, 0, 0)

    override suspend fun getParkingSpots(parkingId: Int): List<ParkingSpot> = emptyList()

    override fun observeSpaceSummary(parkingId: Int): Flow<SpaceSummary> = emptyFlow()

    override fun observeParkingSpots(parkingId: Int): Flow<List<ParkingSpot>> = parkingSpotsFlow

    override suspend fun occupyParkingSpot(parkingId: Int, spaceId: Int) {
        occupyParkingSpotCalled = true
        occupyParkingId = parkingId
        occupySpaceId = spaceId
    }

    override suspend fun releaseParkingSpot(parkingId: Int, spaceId: Int) {
        releaseParkingSpotCalled = true
        releaseParkingId = parkingId
        releaseSpaceId = spaceId
    }
}

class FakeReservationSummaryRepository : ReservationSummaryRepository {
    override suspend fun getActiveReservations(userId: Int): List<ReservationModel> = emptyList()
    override fun observeActiveReservations(userId: Int): Flow<List<ReservationModel>> = emptyFlow()
    override suspend fun checkInReservation(reservationId: Int, parkingId: Int, spaceId: Int) {}
    override suspend fun checkOutReservation(reservationId: Int, parkingId: Int, spaceId: Int) {}
    override suspend fun checkAndEvacuateExpiredReservations() {}
    override suspend fun checkAndCancelReservation(reservationId: Int, parkingId: Int, spaceId: Int) {}
}

class FakeGetReservationSummaryUseCase(
    repository: ReservationSummaryRepository = FakeReservationSummaryRepository()
) : GetReservationSummaryUseCase(repository) {
    
    var evacuateCalled = false

    override suspend fun evacuate() {
        evacuateCalled = true
    }
}
