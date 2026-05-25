package com.easypark.app.signin.presentation.viewmodel

import com.easypark.app.core.FakeAuthRepository
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.signin.domain.usecase.DoLoginUseCase
import com.easypark.app.signin.presentation.state.SignInEffect
import com.easypark.app.signin.presentation.state.SignInEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val repository = FakeAuthRepository()
    private val sessionManager = SessionManager()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var doLoginUseCase: DoLoginUseCase
    private lateinit var viewModel: SignInViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        doLoginUseCase = DoLoginUseCase(repository, sessionManager)
        viewModel = SignInViewModel(doLoginUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenEmailIsInvalid_showsEmailError() = runTest {
        // Arrange
        viewModel.onEvent(SignInEvent.OnEmailChange("invalid-email"))
        viewModel.onEvent(SignInEvent.OnPasswordChange("123456"))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(SignInEvent.OnLoginClick)
            val effect = awaitItem()
            assertTrue(effect is SignInEffect.ShowError)
            assertEquals("Por favor ingresa un correo electrónico válido.", effect.message)
            assertTrue(viewModel.state.value.isEmailError)
        }
    }

    @Test
    fun whenPasswordIsShort_showsPasswordError() = runTest {
        // Arrange
        viewModel.onEvent(SignInEvent.OnEmailChange("test@easypark.com"))
        viewModel.onEvent(SignInEvent.OnPasswordChange("12345"))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(SignInEvent.OnLoginClick)
            val effect = awaitItem()
            assertTrue(effect is SignInEffect.ShowError)
            assertEquals("La contraseña debe tener al menos 6 caracteres.", effect.message)
            assertTrue(viewModel.state.value.isPasswordError)
        }
    }

    @Test
    fun whenLoginSucceeds_isLoadingChangesCorrectly() = runTest {
        // Arrange
        val user = UserModel(
            id = 1,
            name = "Test User",
            type = UserType.OWNER,
            email = "owner@test.com",
            password = "password",
            cellphone = 12345678
        )
        repository.stubbedUser = user
        repository.stubbedParkingId = 10

        viewModel.onEvent(SignInEvent.OnEmailChange("owner@test.com"))
        viewModel.onEvent(SignInEvent.OnPasswordChange("password"))

        // Act & Assert
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            viewModel.onEvent(SignInEvent.OnLoginClick)

            // Loading state
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            // Final state
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
        }
        
        // Assert that repository was called correctly
        assertTrue(repository.loginCalled)
        assertEquals("owner@test.com", repository.loginEmail)
        assertEquals("password", repository.loginPass)
        assertTrue(repository.getParkingIdCalled)
        assertEquals(1, repository.getParkingIdOwnerId)

        // Assert that sessionManager actually saved the session!
        assertEquals(user, sessionManager.currentUser.value)
        assertEquals(10, sessionManager.currentParkingId)
    }

    @Test
    fun whenLoginSucceedsAsOwner_emitsNavigateToHomeWithOwnerRole() = runTest {
        // Arrange
        val user = UserModel(
            id = 1,
            name = "Owner User",
            type = UserType.OWNER,
            email = "owner@test.com",
            password = "password",
            cellphone = 12345678
        )
        repository.stubbedUser = user
        repository.stubbedParkingId = 10

        viewModel.onEvent(SignInEvent.OnEmailChange("owner@test.com"))
        viewModel.onEvent(SignInEvent.OnPasswordChange("password"))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(SignInEvent.OnLoginClick)
            val effect = awaitItem()
            assertTrue(effect is SignInEffect.NavigateToHome)
            assertEquals("OWNER", effect.userType)
        }
    }

    @Test
    fun whenLoginFails_emitsShowError() = runTest {
        // Arrange
        repository.stubbedUser = null

        viewModel.onEvent(SignInEvent.OnEmailChange("wrong@test.com"))
        viewModel.onEvent(SignInEvent.OnPasswordChange("wrongpass"))

        // Act & Assert
        viewModel.effect.test {
            viewModel.onEvent(SignInEvent.OnLoginClick)
            val effect = awaitItem()
            assertTrue(effect is SignInEffect.ShowError)
            assertEquals("Email o contraseña incorrectos", effect.message)
        }
    }
}
