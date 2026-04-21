package com.easypark.app.findparking.presentation.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.easypark.app.findparking.presentation.state.FindParkingUIState
import com.easypark.app.registerparking.domain.model.ParkingModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
actual fun FindParkingMapComponent(
    state: FindParkingUIState,
    onMarkerClick: (ParkingModel) -> Unit,
    modifier: Modifier // Quita el "= Modifier" aquí, solo se pone en el expect
) {
    Text("Mapa no disponible en iOS")
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(state.mapCenter.first, state.mapCenter.second))
            }
        },
        update = { mapView ->
            mapView.overlays.clear() // Limpiar para redibujar marcadores

            state.allParkings .forEach { parking ->
                val marker = org.osmdroid.views.overlay.Marker(mapView)
                marker.position = GeoPoint(parking.latitude, parking.longitude)
                marker.title = parking.name
                marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)

                // Cambiar color del icono si es posible o usar uno propio
                marker.setOnMarkerClickListener { m, _ ->
                    onMarkerClick(parking)
                    true
                }
                mapView.overlays.add(marker)
            }

            // Si el buscador cambió el centro, mover la cámara
            mapView.controller.animateTo(GeoPoint(state.mapCenter.first, state.mapCenter.second))
            mapView.invalidate()
        }
    )
}