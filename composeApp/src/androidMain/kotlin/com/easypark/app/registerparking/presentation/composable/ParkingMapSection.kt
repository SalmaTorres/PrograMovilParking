package com.easypark.app.registerparking.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState
import com.easypark.app.shared.ui.ParkBlue
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
actual fun ParkingMapSection(
    state: RegisterParkingUIState, // Debe llamarse 'state' igual que el expect
    onLocationChanged: (Double, Double) -> Unit // Debe llamarse igual
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(state.latitude, state.longitude))

                    addMapListener(object : org.osmdroid.events.MapListener {
                        override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                            val center = mapCenter as? GeoPoint
                            center?.let { onLocationChanged(it.latitude, it.longitude) }
                            return true
                        }
                        override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean = false
                    })
                }
            }
        )

        Surface(
            modifier = Modifier.size(45.dp).align(Alignment.Center).shadow(4.dp, CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            Icon(Icons.Default.Search, null, Modifier.padding(10.dp), tint = ParkBlue)
        }
    }
}