package com.easypark.app.findparking.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.registerparking.domain.model.ParkingModel
import com.easypark.app.core.presentation.composable.ParkButton
import com.easypark.app.core.presentation.composable.ParkCard
import com.easypark.app.core.ui.*
import kotlinproject.composeapp.generated.resources.*
import kotlinproject.composeapp.generated.resources.status_available
import kotlinproject.composeapp.generated.resources.status_full
import org.jetbrains.compose.resources.stringResource

@Composable
fun ParkingDetailCard(
    parking: ParkingModel,
    onReserve: () -> Unit,
    onDetails: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ParkCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {

            androidx.compose.material3.IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .size(28.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = null,
                    tint = ParkGray,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp)) {
                Text(
                    text = parking.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ParkTextDark,
                    modifier = Modifier.padding(end = 24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${parking.pricePerHour.format()} / h",
                        color = ParkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = if (parking.isAvailable)
                            stringResource(Res.string.status_available)
                        else
                            stringResource(Res.string.status_full),
                        color = if (parking.isAvailable) ParkSuccess else ParkError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ParkButton(
                            text = stringResource(Res.string.action_reserve),
                            onClick = onReserve,
                            enabled = parking.isAvailable,
                            modifier = Modifier.height(38.dp)
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ParkButton(
                            text = stringResource(Res.string.action_see_more),
                            onClick = onDetails,
                            isSecondary = true,
                            modifier = Modifier.height(38.dp)
                        )
                    }
                }
            }
        }
    }
}