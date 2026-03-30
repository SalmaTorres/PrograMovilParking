package com.easypark.app.register.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.register.presentation.state.*
import com.easypark.app.register.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.register_bg

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateLogin: () -> Unit,
    onNavigateNext: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin -> onNavigateLogin()
                RegisterEffect.NavigateToNext -> onNavigateNext()
                is RegisterEffect.ShowError -> println(effect.message)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {

            Image(
                painter = painterResource(Res.drawable.register_bg),
                contentDescription = null,
                modifier = Modifier.matchParentSize()
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

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 INPUTS
        Text("Nombre Completo")
        TextField(
            value = state.name,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnNameChange(it))
            },
            placeholder = { Text("Introduce tu nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Correo electronico")
        TextField(
            value = state.email,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnEmailChange(it))
            },
            placeholder = { Text("Introduce tu correo electronico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Numero de celular")
        TextField(
            value = state.phone,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnPhoneChange(it))
            },
            placeholder = { Text("Introduce tu numero de celular") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Contraseña")
        TextField(
            value = state.password,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnPasswordChange(it))
            },
            placeholder = { Text("Crea una contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(RegisterEvent.OnRegisterClick)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Siguiente")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                viewModel.onEvent(RegisterEvent.OnLoginClick)
            }
        ) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }
    }
}