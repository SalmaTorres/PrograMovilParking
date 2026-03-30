package com.easypark.app.signin.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkTextField
import com.easypark.app.signin.presentation.state.*
import com.easypark.app.signin.presentation.viewmodel.SignInViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.easypark_logo // 👈 debes crear esto
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = koinViewModel ()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SignInEffect.NavigateToHome -> {
                    TODO()
                }
                is SignInEffect.NavigateToRegister -> {
                    navController.navigate(NavRoute.Register)
                }
                is SignInEffect.ShowError -> {
                    println(effect.message)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.easypark_logo),
            contentDescription = null,
            modifier = Modifier.size(155.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ParkTextField(
            value = state.email,
            onValueChange = {
                viewModel.onEvent(SignInEvent.OnEmailChange(it))
            },
            placeholder = "name@company.com",
            isError = state.isEmailError,
            label = "Correo"
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParkTextField(
            value = state.password,
            onValueChange = {
                viewModel.onEvent(SignInEvent.OnPasswordChange(it))
            },
            placeholder = "********",
            isError = state.isPasswordError,
            isPassword = true,
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(24.dp))

        ParkButton(
            onClick = {
                viewModel.onEvent(SignInEvent.OnLoginClick)
            },
            text = "Iniciar Sesion"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("O")

        Spacer(modifier = Modifier.height(16.dp))

        ParkButton(
            onClick = {
                viewModel.onEvent(SignInEvent.OnRegisterClick)
            },
            text = "Crear una cuenta",
            isSecondary = true
        )
    }
}