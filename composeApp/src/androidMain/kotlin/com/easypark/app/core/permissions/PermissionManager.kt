package com.easypark.app.core.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
actual fun rememberPermissionManager(): PermissionManager {
    var callback by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Si todos los permisos solicitados fueron concedidos
        val allGranted = results.values.isNotEmpty() && results.values.all { it }
        callback?.invoke(allGranted)
        callback = null // Limpiar callback
    }

    return remember(launcher) {
        object : PermissionManager {
            override fun requestPermission(type: PermissionType, onResult: (Boolean) -> Unit) {
                callback = onResult

                val permissionsToRequest = when (type) {
                    PermissionType.LOCATION -> arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    PermissionType.NOTIFICATIONS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            emptyArray()
                        }
                    }
                    PermissionType.CAMERA -> arrayOf(Manifest.permission.CAMERA)
                }

                if (permissionsToRequest.isEmpty()) {
                    onResult(true) // Ya tiene permiso implícito por versión de SO
                    callback = null
                } else {
                    launcher.launch(permissionsToRequest)
                }
            }
        }
    }
}
