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
import com.easypark.app.core.domain.model.status.UserType
import com.easypark.app.core.presentation.composable.ParkButton
import com.easypark.app.core.presentation.composable.ParkTextField
import com.easypark.app.signin.presentation.state.*
import com.easypark.app.signin.presentation.viewmodel.SignInViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.easypark_logo
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SignInEffect.NavigateToHome -> {
                    if (effect.userType == UserType.OWNER) {
                        navController.navigate(NavRoute.SpaceManagement) {
                            popUpTo(0)
                        }
                    } else if (effect.userType == UserType.DRIVER) {
                        navController.navigate(NavRoute.FindParking) {
                            popUpTo(0)
                        }
                    }
                }
                is SignInEffect.NavigateToRegister -> {
                    navController.navigate(NavRoute.Register)
                }
                is SignInEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding().imePadding()
            ) 
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                placeholder = stringResource(Res.string.hint_email),
                isError = state.isEmailError,
                label = stringResource(Res.string.label_email)
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
                label = stringResource(Res.string.label_password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ParkButton(
                onClick = {
                    viewModel.onEvent(SignInEvent.OnLoginClick)
                },
                text = stringResource(Res.string.action_signin)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(Res.string.signin_or_separator))

            Spacer(modifier = Modifier.height(16.dp))

            ParkButton(
                onClick = {
                    viewModel.onEvent(SignInEvent.OnRegisterClick)
                },
                text = stringResource(Res.string.action_create_account),
                isSecondary = true
            )
        }
    }
}
