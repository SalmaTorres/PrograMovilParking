package com.easypark.app.registervehicle.presentation.viewmodel

import com.easypark.app.core.FakeRegisterVehicleRepository
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.model.status.VehicleType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.registervehicle.domain.model.VehicleModel
import com.easypark.app.registervehicle.domain.usecase.RegisterVehicleUseCase
import com.easypark.app.registervehicle.presentation.state.RegisterVehicleEffect
import com.easypark.app.registervehicle.presentation.state.RegisterVehicleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterVehicleViewModelTest {

    private val repository = FakeRegisterVehicleRepository()
    private val sessionManager = SessionManager()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: RegisterVehicleUseCase
    private lateinit var viewModel: RegisterVehicleViewModel

    private val testUser = UserModel(
        id = 0,
        name = "Driver User",
        type = UserType.DRIVER,
        email = "driver@test.com",
        password = "password123",
        cellphone = 76543210
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = RegisterVehicleUseCase(repository)
        viewModel = RegisterVehicleViewModel(useCase, sessionManager)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenPlateIsEmpty_submitShowsPlateError() = runTest {
        // Arrange
        viewModel.initUser(testUser)
        viewModel.onEvent(RegisterVehicleEvent.OnPlateChange(""))

        // Act
        viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick)

        // Assert
        assertTrue(viewModel.state.value.isPlateError)
    }

    @Test
    fun whenPlateLengthIsGreaterThan7_viewModelIgnoresAssignment() = runTest {
        // Arrange
        // We type "1234567" (7 chars)
        viewModel.onEvent(RegisterVehicleEvent.OnPlateChange("1234567"))
        assertEquals("1234567", viewModel.state.value.plate)

        // Act
        // We try typing another character to make it 8 chars "12345678"
        viewModel.onEvent(RegisterVehicleEvent.OnPlateChange("12345678"))

        // Assert
        // The plate state remains "1234567" because the viewmodel restricts changes to <= 7 chars
        assertEquals("1234567", viewModel.state.value.plate)
    }

    @Test
    fun whenInitUser_storesCorrectUserAndPassesToUseCaseOnSubmit() = runTest {
        // Arrange
        repository.stubbedRegisteredUserId = 42

        viewModel.initUser(testUser)
        viewModel.onEvent(RegisterVehicleEvent.OnPlateChange("1234abc"))
        viewModel.onEvent(RegisterVehicleEvent.OnTypeChange(VehicleType.AUTOMOVIL))

        // Act
        viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick)
        advanceUntilIdle()

        // Assert
        assertTrue(repository.completeDriverRegistrationCalled)
        assertEquals(testUser, repository.registeredUser)
        
        val vehicle = repository.registeredVehicle
        assertEquals("1234ABC", vehicle?.plate)
        assertEquals(VehicleType.AUTOMOVIL.displayName, vehicle?.type)
    }

    @Test
    fun whenRegistrationSucceeds_savesSessionAndEmitsNavigateNext() = runTest {
        // Arrange
        repository.stubbedRegisteredUserId = 101

        viewModel.initUser(testUser)
        viewModel.onEvent(RegisterVehicleEvent.OnPlateChange("777xyz"))
        viewModel.onEvent(RegisterVehicleEvent.OnTypeChange(VehicleType.MOTOCICLETA))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick)

            val effect = awaitItem()
            assertTrue(effect is RegisterVehicleEffect.NavigateNext)
        }

        // Verify sessionManager saves the modified user with ID 101 and null parkingId
        val expectedSavedUser = testUser.copy(id = 101)
        assertEquals(expectedSavedUser, sessionManager.currentUser.value)
        assertEquals(null, sessionManager.currentParkingId)
    }

    @Test
    fun whenRegistrationFails_emitsShowError() = runTest {
        // Arrange
        repository.stubbedRegisteredUserId = null

        viewModel.initUser(testUser)
        viewModel.onEvent(RegisterVehicleEvent.OnPlateChange("777xyz"))
        viewModel.onEvent(RegisterVehicleEvent.OnTypeChange(VehicleType.NINGUNO))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterVehicleEvent.OnSubmitClick)

            val effect = awaitItem()
            assertTrue(effect is RegisterVehicleEffect.ShowError)
            assertEquals("Error al registrar el vehículo", effect.message)
        }
    }
}
