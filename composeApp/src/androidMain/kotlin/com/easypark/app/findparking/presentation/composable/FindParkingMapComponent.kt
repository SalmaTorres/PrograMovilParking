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
    modifier: Modifier
) {
    val context = LocalContext.current

    val parkingsToShow = when {
        state.selectedParking != null -> {
            state.allParkings
        }
        state.searchQuery.isEmpty() -> state.allParkings
        else -> state.suggestions
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

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