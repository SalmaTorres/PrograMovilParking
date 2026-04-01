package com.easypark.app.register.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.register.presentation.state.*
import com.easypark.app.register.presentation.viewmodel.RegisterViewModel
import com.easypark.app.shared.domain.model.UserType
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkTextField
import com.easypark.app.shared.ui.ParkBlue
import com.easypark.app.shared.ui.ParkBlueLight
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.register_bg
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin -> {
                    navController.navigate(NavRoute.SignIn)
                }
                RegisterEffect.NavigateToRegisterVehicle -> {
                    navController.navigate(NavRoute.RegisterParking)
                }
                RegisterEffect.NavigateToRegisterParking -> {
                    navController.navigate(NavRoute.RegisterParking)
                }
                is RegisterEffect.ShowError -> {
                    println("Error: ${effect.message}")
                }
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
                modifier = Modifier.fillMaxWidth(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            IconButton(
                onClick = { viewModel.onEvent(RegisterEvent.OnLoginClick) },
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White
                )
            }

            Text(
                text = "REGISTRATE",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
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
                val isConductor = state.role == UserType.DRIVER
                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConductor) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                ) {
                    Text("Conductor", color = if (isConductor) Color.White else Color.Black)
                }

                val isDueno = state.role == UserType.OWNER
                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.OWNER)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDueno) ParkBlue else ParkBlueLight
                    )
                ) {
                    Text("Dueño", color = if (isDueno) Color.White else Color.Black)
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