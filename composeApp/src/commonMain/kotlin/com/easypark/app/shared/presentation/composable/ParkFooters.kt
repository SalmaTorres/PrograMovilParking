package com.easypark.app.shared.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.shared.ui.ParkBlue
import com.easypark.app.shared.ui.ParkGray
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_calendar
import kotlinproject.composeapp.generated.resources.ic_garage
import kotlinproject.composeapp.generated.resources.ic_home
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.layout.navigationBarsPadding
import com.easypark.app.navigation.NavRoute

@Composable
private fun FooterItem(
    label: String,
    imageRes: org.jetbrains.compose.resources.DrawableResource,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) ParkBlue else ParkGray

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(top = 5.dp, start = 12.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color) // Esto pintará tu JPG del color del estado
        )
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// --- FOOTER CONDUCTOR ---
@Composable
fun DriverFooter(
    currentRoute: NavRoute,
    onNavigate: (NavRoute) -> Unit
) {
    Surface(color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FooterItem(
                label = "Inicio",
                imageRes = Res.drawable.ic_home,
                isSelected = currentRoute is NavRoute.FindParking,
                onClick = { onNavigate(NavRoute.FindParking) }
            )
            FooterItem(
                label = "Mis Reservas",
                imageRes = Res.drawable.ic_calendar,
                isSelected = currentRoute is NavRoute.ReservationSummary,
                onClick = { onNavigate(NavRoute.ReservationSummary) }
            )
        }
    }
}

// --- FOOTER DUEÑO ---
@Composable
fun OwnerFooter(
    currentRoute: NavRoute,
    onNavigate: (NavRoute) -> Unit
) {
    Surface(color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FooterItem(
                label = "Reservas",
                imageRes = Res.drawable.ic_calendar,
                isSelected = currentRoute is NavRoute.ReservationHistory,
                onClick = { onNavigate(NavRoute.ReservationHistory) }
            )
            FooterItem(
                label = "Inicio",
                imageRes = Res.drawable.ic_home,
                isSelected = currentRoute is NavRoute.Earnings,
                onClick = { onNavigate(NavRoute.Earnings) }
            )
            FooterItem(
                label = "Espacios",
                imageRes = Res.drawable.ic_garage,
                isSelected = currentRoute is NavRoute.SpaceManagement,
                onClick = { onNavigate(NavRoute.SpaceManagement) }
            )
        }
    }
}