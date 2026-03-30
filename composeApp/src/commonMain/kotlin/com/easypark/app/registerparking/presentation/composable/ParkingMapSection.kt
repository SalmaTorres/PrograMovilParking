package com.easypark.app.registerparking.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState
import com.easypark.app.shared.ui.ParkBlue
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ParkingMapSection(
    state: RegisterParkingUIState,
    onLocationChanged: (Double, Double) -> Unit
) {
    // Configuración de la cámara inicial
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(state.latitude, state.longitude),
            15f
        )
    }

    // Detectar cuando el usuario deja de mover el mapa
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val target = cameraPositionState.position.target
            onLocationChanged(target.latitude, target.longitude)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // AQUÍ SE USA EL MapUiSettings de la librería, no una función propia
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true
            )
        )

        // Lupa blanca central (como en tu imagen)
        Surface(
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.Center)
                .shadow(4.dp, CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = ParkBlue
            )
        }
    }
}