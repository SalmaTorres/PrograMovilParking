package com.easypark.app.register.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.register.presentation.state.*
import com.easypark.app.register.presentation.viewmodel.RegisterViewModel
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkTextField
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.register_bg
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = koinViewModel(),
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin -> {
                    navController.navigate(NavRoute.SignIn)
                }
                RegisterEffect.NavigateToNext -> TODO()
                is RegisterEffect.ShowError -> println(effect.message)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {

            Image(
                painter = painterResource(Res.drawable.register_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "REGISTRATE",
                color = Color.White,
                fontSize = 36.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            )
        }

        Column (
            modifier = Modifier.padding(24.dp, 0.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            ParkTextField(
                value = state.name,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnNameChange(it))
                },
                placeholder = "Introduce tu nombre",
                label = "Nombre Completo",
                isError = state.isNameError,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ParkTextField(
                value = state.email,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnEmailChange(it))
                },
                placeholder = "Introduce tu correo electronico",
                label = "Correo electronico",
                isError = state.isEmailError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ParkTextField(
                value = state.phone,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnPhoneChange(it))
                },
                placeholder = "Introduce tu numero de celular",
                label = "Numero de celular",
                isError = state.isPhoneError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ParkTextField(
                value = state.password,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnPasswordChange(it))
                },
                placeholder = "Crea una contraseña",
                isPassword = true,
                label = "Contraseña",
                isError = state.isPasswordError
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Quiero ser...")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        viewModel.onEvent(RegisterEvent.OnRoleSelected("CONDUCTOR"))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Conductor")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.onEvent(RegisterEvent.OnRoleSelected("DUENO"))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dueño")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ParkButton(
                onClick = {
                    viewModel.onEvent(RegisterEvent.OnRegisterClick)
                },
                text = "Siguiente"
            )
        }
    }
}