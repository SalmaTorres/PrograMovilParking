package com.easypark.app.reservationsummary.presentation.screen

import ReservationModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.core.ui.ParkBackground
import com.easypark.app.core.ui.ParkBlue
import com.easypark.app.core.ui.ParkGray
import com.easypark.app.reservationsummary.presentation.composable.DetailRow
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.reservation_summary_cost
import kotlinproject.composeapp.generated.resources.reservation_summary_duration
import kotlinproject.composeapp.generated.resources.reservation_summary_entry_time
import kotlinproject.composeapp.generated.resources.reservation_summary_location
import kotlinproject.composeapp.generated.resources.reservation_summary_payment
import kotlinproject.composeapp.generated.resources.reservation_summary_space
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReservationItemCard(reservation: ReservationModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabecera: Ubicación
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ParkBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(Res.string.reservation_summary_location), color = ParkGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(reservation.parkingName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(reservation.address, color = ParkGray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Detalles de la reserva
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(Icons.Default.Place, stringResource(Res.string.reservation_summary_space), "Nro ${reservation.spaceNumber}")
                DetailRow(Icons.Default.Refresh, stringResource(Res.string.reservation_summary_entry_time), value = "${reservation.startTime}")
                DetailRow(Icons.Default.Build, stringResource(Res.string.reservation_summary_duration), value = reservation.durationText)

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                // Fila de Costo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBox, contentDescription = null, tint = ParkBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.reservation_summary_cost), fontWeight = FontWeight.Medium)
                    }
                    Text("Bs ${reservation.totalPrice.amount}", color = ParkBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila de Pago
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.reservation_summary_payment), fontWeight = FontWeight.Medium)
                    }
                    Text(reservation.paymentMethod, color = ParkGray)
                }
            }
        }
    }
}