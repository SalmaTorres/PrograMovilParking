package com.easypark.app.core.permissions

import androidx.compose.runtime.Composable

interface PermissionManager {
    fun requestPermission(type: PermissionType, onResult: (Boolean) -> Unit)
}

@Composable
expect fun rememberPermissionManager(): PermissionManager
