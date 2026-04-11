package com.easypark.app.register.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.easypark.app.shared.ui.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
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
                    navController.navigate(NavRoute.RegisterVehicle)
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
                    .statusBarsPadding()
                    .padding(start = 8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.back),
                    tint = Color.White
                )
            }

            Text(
                text = stringResource(Res.string.register_title),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, top = 20.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            ParkTextField(
                value = state.name,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnNameChange(it))
                },
                placeholder = stringResource(Res.string.hint_name),
                label = stringResource(Res.string.label_name),
                isError = state.isNameError,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ParkTextField(
                value = state.email,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnEmailChange(it))
                },
                label = stringResource(Res.string.label_email),
                placeholder = stringResource(Res.string.hint_email),
                isError = state.isEmailError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ParkTextField(
                value = state.phone,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnPhoneChange(it))
                },
                label = stringResource(Res.string.label_phone),
                placeholder = stringResource(Res.string.hint_phone),
                isError = state.isPhoneError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ParkTextField(
                value = state.password,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.OnPasswordChange(it))
                },
                label = stringResource(Res.string.label_password),
                placeholder = stringResource(Res.string.hint_password),
                isPassword = true,
                isError = state.isPasswordError
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(Res.string.register_role_selection),
                fontWeight = FontWeight.Bold,
                color = ParkTextDark,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isConductor = state.role == UserType.DRIVER
                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.DRIVER)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConductor) ParkBlue else ParkBlueLight
                    )
                ) {
                    Text(
                        text = stringResource(Res.string.role_driver),
                        color = if (isConductor) Color.White else ParkTextDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                val isDueno = state.role == UserType.OWNER
                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.OnRoleSelected(UserType.OWNER)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDueno) ParkBlue else ParkBlueLight
                    )
                ) {
                    Text(
                        text = stringResource(Res.string.role_owner),
                        color = if (isDueno) Color.White else ParkTextDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ParkButton(
                onClick = {
                    viewModel.onEvent(RegisterEvent.OnRegisterClick)
                },
                text = stringResource(Res.string.action_next),
            )
        }
    }
}