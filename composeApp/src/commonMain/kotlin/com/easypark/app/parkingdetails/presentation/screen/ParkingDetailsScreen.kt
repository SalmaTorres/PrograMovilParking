package com.easypark.app.parkingdetails.presentation.screen

import androidx.compose.runtime.Composable
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsEffect
import com.easypark.app.parkingdetails.presentation.state.ParkingDetailsEvent
import com.easypark.app.core.presentation.composable.ParkButton
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.ui.*
import com.easypark.app.parkingdetails.presentation.composable.DetailItem
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ParkingDetailsScreen(
    parkingId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: (Int) -> Unit,
    viewModel: ParkingDetailsViewModel = koinViewModel { parametersOf(parkingId) }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ParkingDetailsEffect.NavigateBack -> onNavigateBack()
                is ParkingDetailsEffect.NavigateToBooking -> {
                    onNavigateToBooking(effect.id)
                }
                is ParkingDetailsEffect.ShowError -> {
                    println("Error: ${effect.message}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.parking_details_title),
                onBackClick = { viewModel.onEvent(ParkingDetailsEvent.OnBackClick) },
                onNotificationClick = null
            )
        },
        bottomBar = {
            if (!state.isLoading && state.parkingDetail != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    ParkButton(
                        text = stringResource(Res.string.action_reserve),
                        onClick = { viewModel.onEvent(ParkingDetailsEvent.OnReserveClick) },
                        isSecondary = false
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.parkingDetail != null) {
                val detail = state.parkingDetail!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_garage),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = detail.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        var isRatingMode by remember { mutableStateOf(false) }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isRatingMode) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    for (i in 1..5) {
                                        Text(
                                            text = if (i <= state.userRating) "★" else "☆",
                                            color = ParkBlue,
                                            fontSize = 28.sp,
                                            modifier = Modifier
                                                .clickable { viewModel.onEvent(ParkingDetailsEvent.OnRate(i)) }
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                val starsInt = detail.rating?.toInt() ?: 0
                                val displayStars = "★".repeat(starsInt) + "☆".repeat(5 - starsInt)

                                Text(text = displayStars, color = ParkBlue, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))

                                // Mostramos el decimal (ej: 4.3)
                                Text(
                                    text = "${detail.rating ?: 0.0} (${detail.reviewCount} ${stringResource(Res.string.format_reviews)})",
                                    color = ParkGray
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            TextButton(onClick = { isRatingMode = !isRatingMode }) {
                                Text(
                                    text = if (isRatingMode) stringResource(Res.string.action_save) else stringResource(Res.string.action_rate),
                                    color = ParkBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        DetailItem(icon = Icons.Default.LocationOn, text = detail.address, label = "Dirección")

                        Spacer(modifier = Modifier.height(16.dp))

                        DetailItem(icon = Icons.Default.Payments, text = detail.pricePerHour.format(), label = "Precio por hora")

                        Spacer(modifier = Modifier.height(16.dp))

                        DetailItem(icon = Icons.Default.AccessTime, text = detail.schedule ?: "24 Horas", label = "Horario")

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_calendar),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (detail.isAvailable) stringResource(Res.string.status_available) else stringResource(Res.string.status_full),
                                color = if (detail.isAvailable) Color(0xFF00C853) else Color.Red,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
