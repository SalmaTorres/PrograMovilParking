package com.easypark.app.reservationhistory.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.core.ui.*
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReservationCard(
    reservation: ReservationItemModel,
    modifier: Modifier = Modifier
) {
    val statusColor = when (reservation.status) {
        ReservationStatus.ACTIVE -> ParkSuccess
        ReservationStatus.ENDING_SOON -> Color(0xFFF57C00) // Orange
        ReservationStatus.FINISHED -> ParkGray
    }

    val statusText = when (reservation.status) {
        ReservationStatus.ACTIVE -> stringResource(Res.string.status_active)
        ReservationStatus.ENDING_SOON -> reservation.timeLeftText?.uppercase()
            ?: stringResource(Res.string.history_status_ending_soon)
        ReservationStatus.FINISHED -> stringResource(Res.string.status_finished)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = reservation.clientName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ParkTextDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = stringResource(Res.string.label_spaces, reservation.spaceLabel),
                color = ParkGray,
                fontSize = 14.sp
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🕒 ${reservation.startTime} - ${reservation.endTime}",
                    color = ParkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
