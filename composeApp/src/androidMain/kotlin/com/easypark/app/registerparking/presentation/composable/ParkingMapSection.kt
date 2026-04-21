package com.easypark.app.registerparking.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
actual fun ParkingMapSection(
    state: RegisterParkingUIState,
    onLocationChanged: (Double, Double) -> Unit
) {
    Text("Vista de mapa no disponible")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)

                    val startPoint = GeoPoint(state.latitude, state.longitude)
                    controller.setCenter(startPoint)

                    val marker = Marker(this)
                    marker.position = startPoint
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "Ubicación del parqueo"
                    overlays.add(marker)

                    val eventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            marker.position = p
                            invalidate()

                            onLocationChanged(p.latitude, p.longitude)
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }

                    val overlay = MapEventsOverlay(eventsReceiver)
                    overlays.add(overlay)
                }
            },
            update = { mapView ->
                val currentPoint = GeoPoint(state.latitude, state.longitude)
                val marker = mapView.overlays.filterIsInstance<Marker>().firstOrNull()
                marker?.position = currentPoint
                mapView.invalidate()
            }
        )
    }
}