package com.easypark.app.findparking.presentation.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.easypark.app.findparking.presentation.state.FindParkingUIState
import com.easypark.app.registerparking.domain.model.ParkingModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.easypark.app.core.permissions.PermissionType
import com.easypark.app.core.permissions.rememberPermissionManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("MissingPermission")
@Composable
actual fun FindParkingMapComponent(
    state: FindParkingUIState,
    onMarkerClick: (ParkingModel) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val permissionManager = rememberPermissionManager()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var hasCenteredOnUser by remember { mutableStateOf(false) }

    val parkingsToShow = when {
        state.selectedParking != null -> state.allParkings
        state.searchQuery.isEmpty() -> state.allParkings
        else -> state.suggestions
    }

    LaunchedEffect(Unit) {
        permissionManager.requestPermission(PermissionType.LOCATION) { granted ->
            if (granted && !hasCenteredOnUser) {
                try {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            location?.let {
                                val userPoint = GeoPoint(it.latitude, it.longitude)
                                mapViewRef?.controller?.animateTo(userPoint)
                                mapViewRef?.controller?.setZoom(16.0)
                                hasCenteredOnUser = true
                            }
                        }
                } catch (e: Exception) {
                    println("FindParkingMap: Error obteniendo ubicación - ${e.message}")
                }
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)

                // Agregar el overlay para mostrar el punto azul del usuario
                val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                myLocationOverlay.enableMyLocation()
                overlays.add(myLocationOverlay)

                mapViewRef = this
            }
        },
        update = { mapView ->
            // Remover marcadores antiguos conservando el overlay de ubicación
            mapView.overlays.removeAll { it is org.osmdroid.views.overlay.Marker }

            parkingsToShow.forEach { parking ->
                val marker = org.osmdroid.views.overlay.Marker(mapView)
                marker.position = GeoPoint(parking.latitude, parking.longitude)
                marker.title = parking.name

                if (state.selectedParking?.id == parking.id) {
                    val drawable = androidx.core.content.ContextCompat.getDrawable(
                        context,
                        android.R.drawable.btn_star_big_on
                    )
                    drawable?.let { marker.icon = it }

                    mapView.controller.animateTo(marker.position)
                }

                marker.setOnMarkerClickListener { m, _ ->
                    onMarkerClick(parking)
                    m.showInfoWindow()
                    true
                }
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
    )
}