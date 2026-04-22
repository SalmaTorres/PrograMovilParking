package com.easypark.app.core.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPermissionManager(): PermissionManager {
    return remember {
        object : PermissionManager {
            override fun requestPermission(type: PermissionType, onResult: (Boolean) -> Unit) {
                // Implementación stub para iOS. 
                // Aquí deberías integrar la API de iOS para pedir permisos en el futuro.
                // Por ahora simulamos que siempre se acepta.
                onResult(true)
            }
        }
    }
}
