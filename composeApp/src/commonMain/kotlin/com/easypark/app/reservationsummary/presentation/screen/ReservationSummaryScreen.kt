package com.easypark.app.reservationsummary.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.reservationsummary.presentation.viewmodel.ReservationSummaryViewModel
import com.easypark.app.core.presentation.composable.DriverFooter
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.presentation.composable.ParkLoading
import com.easypark.app.core.ui.ParkBlue
import com.easypark.app.core.ui.ParkGray
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReservationSummaryScreen(
    navController: NavController,
    viewModel: ReservationSummaryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.reservation_summary_title),
                onBackClick = { navController.popBackStack() },
                onNotificationClick = null
            )
        },
        bottomBar = {
            DriverFooter(
                currentRoute = NavRoute.ReservationSummary(0),
                onNavigate = { route ->
                    if (route !is NavRoute.ReservationSummary) {
                        navController.navigate(route)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                ParkLoading()
            } else if (state.reservations.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.reservations) { reservation ->
                        ReservationItemCard(reservation = reservation)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = ParkGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No tienes reservas activas",
                        fontWeight = FontWeight.Bold,
                        color = ParkGray,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate(NavRoute.FindParking) },
                        colors = ButtonDefaults.buttonColors(containerColor = ParkBlue)
                    ) {
                        Text("Buscar un parqueo")
                    }
                }
            }
        }
    }
}