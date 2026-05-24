package com.easypark.app.reservationhistory.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    modifier: Modifier = Modifier,
    onCheckInClick: () -> Unit = {}
) {
    // 1. Definición de colores por estado
    val statusColor = when (reservation.status) {
        ReservationStatus.OCUPADO, ReservationStatus.ACTIVE -> ParkSuccess
        ReservationStatus.PENDIENTE -> Color(0xFFFBC02D) // Amarillo/Naranja para espera
        ReservationStatus.ENDING_SOON -> Color(0xFFF57C00) // Naranja fuerte
        ReservationStatus.FINISHED -> ParkGray
        ReservationStatus.CANCELADO -> Color(0xFFD32F2F) // Rojo
    }

    // 2. Definición de texto por estado
    val statusText = when (reservation.status) {
        ReservationStatus.OCUPADO, ReservationStatus.ACTIVE -> stringResource(Res.string.status_active)
        ReservationStatus.PENDIENTE -> "PENDIENTE"
        ReservationStatus.ENDING_SOON -> reservation.timeLeftText?.uppercase() ?: "TERMINA PRONTO"
        ReservationStatus.FINISHED -> stringResource(Res.string.status_finished)
        ReservationStatus.CANCELADO -> "CANCELADO"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Fila de Estado
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

            // Nombre del Cliente
            Text(
                text = reservation.clientName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ParkTextDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // DATOS DEL VEHÍCULO (Realismo para el Dueño)
            if (!reservation.vehiclePlate.isNullOrEmpty()) {
                Text(
                    text = "${reservation.vehiclePlate} • ${reservation.vehicleType}",
                    color = ParkBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Espacio y Horario
            Text(
                text = stringResource(Res.string.label_spaces, reservation.spaceLabel),
                color = ParkGray,
                fontSize = 14.sp
            )

            Text(
                text = "${reservation.startTime} - ${reservation.endTime}",
                color = ParkGray,
                fontSize = 14.sp
            )

            // LÓGICA DEL BOTÓN: Solo aparece si está PENDIENTE
            if (reservation.status == ReservationStatus.PENDIENTE) {
                Spacer(modifier = Modifier.height(12.dp))
                androidx.compose.material3.Button(
                    onClick = onCheckInClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = ParkSuccess
                    )
                ) {
                    Text("Marcar Llegada (Check-in)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}