package com.easypark.app.register.presentation.viewmodel

import com.easypark.app.core.FakeRegisterRepository
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.register.domain.usecase.DoRegisterUseCase
import com.easypark.app.register.presentation.state.RegisterEffect
import com.easypark.app.register.presentation.state.RegisterEvent
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
class RegisterViewModelTest {

    private val repository = FakeRegisterRepository()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var doRegisterUseCase: DoRegisterUseCase
    private lateinit var viewModel: RegisterViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        doRegisterUseCase = DoRegisterUseCase(repository)
        viewModel = RegisterViewModel(doRegisterUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenEmailIsInvalid_showsEmailError() = runTest {
        // Arrange
        viewModel.onEvent(RegisterEvent.OnNameChange("Test Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("correo_no_valido"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("123456"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("71234567"))
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.ShowError)
            assertEquals("Email inválido", effect.message)
            assertTrue(viewModel.state.value.isEmailError)
        }
    }

    @Test
    fun whenPasswordIsShort_showsPasswordError() = runTest {
        // Arrange
        viewModel.onEvent(RegisterEvent.OnNameChange("Test Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("test@easypark.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("12345"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("71234567"))
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.ShowError)
            assertEquals("La contraseña debe tener 6+ caracteres", effect.message)
            assertTrue(viewModel.state.value.isPasswordError)
        }
    }

    @Test
    fun whenPhoneIsShort_showsPhoneError() = runTest {
        // Arrange
        viewModel.onEvent(RegisterEvent.OnNameChange("Test Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("test@easypark.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("123456"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("7123456")) // 7 digits
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.ShowError)
            assertEquals("El teléfono debe tener 8 dígitos", effect.message)
            assertTrue(viewModel.state.value.isPhoneError)
        }
    }

    @Test
    fun whenPhoneHasNonDigits_showsPhoneError() = runTest {
        // Arrange
        viewModel.onEvent(RegisterEvent.OnNameChange("Test Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("test@easypark.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("123456"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("7123456A")) // Non-digits
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.ShowError)
            assertEquals("El teléfono debe tener 8 dígitos", effect.message)
            assertTrue(viewModel.state.value.isPhoneError)
        }
    }

    @Test
    fun whenEmailAlreadyExists_showsEmailRegisteredError() = runTest {
        // Arrange
        repository.stubbedEmailAvailable = false
        
        viewModel.onEvent(RegisterEvent.OnNameChange("Test Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("existing@test.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("123456"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("71234567"))
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.ShowError)
            assertEquals("Email ya registrado", effect.message)
        }
        
        assertTrue(repository.isEmailAvailableCalled)
        assertEquals("existing@test.com", repository.isEmailAvailableEmail)
    }

    @Test
    fun whenRegistrationSucceedsAsDriver_navigatesToRegisterVehicle() = runTest {
        // Arrange
        repository.stubbedEmailAvailable = true

        viewModel.onEvent(RegisterEvent.OnNameChange("Driver Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("driver@test.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("password123"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("76543210"))
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.NavigateToRegisterVehicle)
            
            val expectedId = "driver@test.com".replace(".", "_").hashCode() and 0x7FFFFFFF
            val expectedUser = UserModel(
                id = expectedId,
                name = "Driver Name",
                type = UserType.DRIVER,
                email = "driver@test.com",
                password = "password123",
                cellphone = 76543210
            )
            assertEquals(expectedUser, effect.user)
        }
    }

    @Test
    fun whenRegistrationSucceedsAsOwner_navigatesToRegisterParking() = runTest {
        // Arrange
        repository.stubbedEmailAvailable = true

        viewModel.onEvent(RegisterEvent.OnNameChange("Owner Name"))
        viewModel.onEvent(RegisterEvent.OnEmailChange("owner@test.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChange("password123"))
        viewModel.onEvent(RegisterEvent.OnPhoneChange("76543210"))
        viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.OWNER))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.NavigateToRegisterParking)

            val expectedId = "owner@test.com".replace(".", "_").hashCode() and 0x7FFFFFFF
            val expectedUser = UserModel(
                id = expectedId,
                name = "Owner Name",
                type = UserType.OWNER,
                email = "owner@test.com",
                password = "password123",
                cellphone = 76543210
            )
            assertEquals(expectedUser, effect.user)
        }
    }
}
