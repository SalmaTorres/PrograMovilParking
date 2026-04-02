package com.easypark.app.findparking.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.findparking.presentation.state.FindParkingEvent
import com.easypark.app.findparking.presentation.viewmodel.FindParkingViewModel
import com.easypark.app.navigation.NavRoute
import com.easypark.app.shared.presentation.composable.DriverFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import com.easypark.app.shared.presentation.composable.ParkTextField
import org.koin.compose.viewmodel.koinViewModel
import com.easypark.app.findparking.presentation.composable.FindParkingMapComponent
import com.easypark.app.findparking.presentation.composable.ParkingDetailCard
import com.easypark.app.findparking.presentation.state.FindParkingEffect
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_home
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FindParkingScreen(
    navController: NavHostController,
    viewModel: FindParkingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FindParkingEffect.MoveCamera -> { /* Lógica para mover el mapa */ }
                is FindParkingEffect.NavigateToBooking -> {
                    navController.navigate(NavRoute.BookingConfirmation(effect.parkingId))
                }
                is FindParkingEffect.NavigateToDetails -> {
                    navController.navigate(NavRoute.ParkingDetails(effect.parkingId))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = "Encuentra tu Lugar",
                onNotificationClick = { navController.navigate(NavRoute.Notifications) }
            )
        },
        bottomBar = {
            DriverFooter(
                currentRoute = NavRoute.FindParking,
                onNavigate = { navController.navigate(it) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            FindParkingMapComponent(
                state = state,
                onMarkerClick = { viewModel.onEvent(FindParkingEvent.OnMarkerClicked(it)) }
            )

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                ParkTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(FindParkingEvent.OnQueryChanged(it)) },
                    placeholder = "Busca tu parqueo...",
                    leadingImage = Res.drawable.ic_home
                )
                // Lista de sugerencias
                if (state.suggestions.isNotEmpty() && state.searchQuery.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Column {
                            state.suggestions.take(5).forEach { parking ->
                                Text(
                                    text = parking.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.onEvent(FindParkingEvent.OnSuggestionSelected(parking)) }
                                        .padding(16.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // CAPA 3: CARD DE DETALLES (FLOTANTE ABAJO)
            state.selectedParking?.let { parking ->
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                    ParkingDetailCard(
                        parking = parking,
                        onReserve = { viewModel.onEvent(FindParkingEvent.OnReserveClick) },
                        onDetails = { viewModel.onEvent(FindParkingEvent.OnDetailsClick) },
                        )
                }
            }
        }
    }
}