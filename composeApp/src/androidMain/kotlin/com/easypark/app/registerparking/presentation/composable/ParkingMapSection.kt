package com.easypark.app.registerparking.presentation.composable

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState
import com.easypark.app.shared.ui.ParkBlue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("MissingPermission")
@Composable
actual fun ParkingMapSection(
    state: RegisterParkingUIState,
    onLocationChanged: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var hasInitialCenter by remember { mutableStateOf(false) }

    val updateToCurrentLocation = {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        onLocationChanged(it.latitude, it.longitude)
                        mapViewRef?.controller?.animateTo(GeoPoint(it.latitude, it.longitude))
                    }
                }
        } catch (e: SecurityException) {
            println("Error de permisos: ${e.message}")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            updateToCurrentLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(17.0)

                    val selectionMarker = Marker(this)
                    selectionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(selectionMarker)

                    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    myLocationOverlay.enableMyLocation()
                    overlays.add(myLocationOverlay)

                    val eventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            onLocationChanged(p.latitude, p.longitude)
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(eventsReceiver))

                    mapViewRef = this
                }
            },
            update = { mapView ->
                val currentPoint = GeoPoint(state.latitude, state.longitude)
                val marker = mapView.overlays.filterIsInstance<Marker>().firstOrNull()
                marker?.position = currentPoint

                if (!hasInitialCenter && state.latitude != 0.0) {
                    mapView.controller.setCenter(currentPoint)
                    hasInitialCenter = true
                }
                mapView.invalidate()
            }
        )

        IconButton(
            onClick = { updateToCurrentLocation() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(Color.White, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Mi ubicación",
                tint = ParkBlue
            )
        }
    }
}