package com.easypark.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easypark.app.core.permissions.PermissionType
import com.easypark.app.core.permissions.rememberPermissionManager
import com.easypark.app.core.ui.ParkBackground
import com.easypark.app.core.work.rememberBackgroundTaskManager
import com.easypark.app.navigation.AppNavHost

import com.easypark.app.core.data.remote.FirebaseManager

@Composable
fun App() {
    val firebaseManager = remember { FirebaseManager() }

    LaunchedEffect(Unit) {
        val token = firebaseManager.getFCMToken()
        println("FCM TOKEN: $token")
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ParkBackground
        ) {
            AppNavHost()
        }
    }
}
