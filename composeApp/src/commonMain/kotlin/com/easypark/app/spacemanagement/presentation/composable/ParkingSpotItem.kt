package com.easypark.app.spacemanagement.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.core.ui.ParkError
import com.easypark.app.core.ui.ParkSuccess
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ParkingSpotItem(
    spot: ParkingSpot,
    onClick: () -> Unit
) {
    val state = spot.state
    val borderColor = when (state) {
        "OCUPADO" -> Color(0xFFEF4444)
        "RESERVADO" -> Color(0xFFF97316)
        else -> Color(0xFF22C55E)
    }
    val bgColor = when (state) {
        "OCUPADO" -> Color(0xFFFEF2F2)
        "RESERVADO" -> Color(0xFFFFF7ED)
        else -> Color(0xFFF0FDF4)
    }
    val textColor = when (state) {
        "OCUPADO" -> Color(0xFFDC2626)
        "RESERVADO" -> Color(0xFFEA580C)
        else -> Color(0xFF16A34A)
    }
    val statusText = when (state) {
        "OCUPADO" -> "Ocupado"
        "RESERVADO" -> "Reservado"
        else -> "Disponible"
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(color = bgColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = spot.number.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}