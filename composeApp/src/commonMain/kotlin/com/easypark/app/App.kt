package com.easypark.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.core.ui.ParkBackground
import com.easypark.app.navigation.AppNavHost
import com.easypark.app.navigation.NavRoute
import com.easypark.app.onboarding.data.OnboardingPreferences
import org.koin.compose.koinInject

@Composable
fun App() {
    val remoteConfig = koinInject<RemoteConfigManager>()
    val onboardingPrefs = koinInject<OnboardingPreferences>()

    var isLoading by remember { mutableStateOf(true) }
    var isMaintenanceMode by remember { mutableStateOf(false) }
    var maintenanceMessage by remember { mutableStateOf("") }

    // Decide el destino inicial una sola vez al arrancar
    val startDestination: Any = remember {
        if (onboardingPrefs.isOnboardingCompleted()) NavRoute.SignIn else NavRoute.Onboarding
    }

    LaunchedEffect(Unit) {
        try {
            remoteConfig.initialize()
            isMaintenanceMode = remoteConfig.getBoolean("app_mantenimiento")
            maintenanceMessage = remoteConfig.getString("mensaje_mantenimiento")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ParkBackground
        ) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (isMaintenanceMode) {
                MaintenanceUI(maintenanceMessage)
            } else {
                AppNavHost(startDestination = startDestination)
            }
        }
    }
}


@Composable
fun MaintenanceUI(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aviso Importante",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (message.isEmpty()) "El sistema se encuentra en mantenimiento. Por favor, intenta más tarde." else message,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}