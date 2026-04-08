package com.easypark.app.parkingdetails.presentation.screen

import androidx.compose.runtime.Composable
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
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
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkHeader
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParkingDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    viewModel: ParkingDetailsViewModel = koinViewModel()
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
                        text = stringResource(Res.string.reserve_button),
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
                                            color = Color.Blue,
                                            fontSize = 28.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    val newRating = if (state.userRating == i) 0 else i
                                                    viewModel.onEvent(ParkingDetailsEvent.OnRate(newRating))
                                                }
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                // Muestra las estrellas seleccionadas si ya calificó, si no muestra hardcoded
                                val displayStars = if (state.userRating > 0) {
                                    "★".repeat(state.userRating) + "☆".repeat(5 - state.userRating)
                                } else {
                                    "★★★☆☆"
                                }
                                Text(text = displayStars, color = Color.Blue, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${detail.rating.toInt()} (${detail.reviewCount} ${stringResource(Res.string.reviews)})",
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = { isRatingMode = !isRatingMode }) {
                                Text(
                                    text = if (isRatingMode) "Guardar" else stringResource(Res.string.rate_button),
                                    color = Color.Blue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_garage),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = detail.address, color = Color.Gray)
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = { /* TODO */ }) {
                                Text(text = stringResource(Res.string.view_map_button), color = Color.Blue)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_garage),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = detail.pricePerHour, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_garage),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = detail.schedule, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_calendar),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (detail.isAvailable) stringResource(Res.string.available) else stringResource(Res.string.not_available),
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
