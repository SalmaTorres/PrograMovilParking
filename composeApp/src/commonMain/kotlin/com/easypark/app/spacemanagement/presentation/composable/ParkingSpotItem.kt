package com.easypark.app.spacemanagement.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.easypark.app.shared.ui.ParkError
import com.easypark.app.shared.ui.ParkSuccess
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ParkingSpotItem(spot: ParkingSpot) {
    val borderColor = if (spot.isOccupied) Color(0xFFFCA5A5) else Color(0xFF86EFAC)
    val bgColor = if (spot.isOccupied) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
    val textColor = if (spot.isOccupied) ParkSuccess else ParkError

    val statusText = if (spot.isOccupied)
        stringResource(Res.string.status_occupied)
    else
        stringResource(Res.string.status_free)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(color = bgColor, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = spot.code,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}