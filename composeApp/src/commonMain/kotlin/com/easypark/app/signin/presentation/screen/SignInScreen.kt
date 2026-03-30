package com.easypark.app.signin.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.signin.presentation.state.*
import com.easypark.app.signin.presentation.viewmodel.SignInViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.easypark_logo // 👈 debes crear esto

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onNavigateHome: () -> Unit,
    onNavigateRegister: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SignInEffect.NavigateToHome -> onNavigateHome()
                SignInEffect.NavigateToRegister -> onNavigateRegister()
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

        // 🔹 LOGO
        Image(
            painter = painterResource(Res.drawable.easypark_logo),
            contentDescription = null,
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Correo")
        TextField(
            value = state.email,
            onValueChange = {
                viewModel.onEvent(SignInEvent.OnEmailChange(it))
            },
            placeholder = { Text("name@company.com") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Contraseña")
        TextField(
            value = state.password,
            onValueChange = {
                viewModel.onEvent(SignInEvent.OnPasswordChange(it))
            },
            placeholder = { Text("********") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onEvent(SignInEvent.OnLoginClick)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Inicia sesión", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("O")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                viewModel.onEvent(SignInEvent.OnRegisterClick)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear una cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By logging in, you agree to our Terms of Service and Privacy Policy.",
            fontSize = 12.sp
        )
    }
}