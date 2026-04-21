package com.easypark.app.reservationhistory.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.core.domain.model.status.ReservationStatus
import com.easypark.app.reservationhistory.presentation.composable.ReservationCard
import com.easypark.app.reservationhistory.presentation.composable.ReservationTabRow
import com.easypark.app.reservationhistory.presentation.viewmodel.ReservationHistoryViewModel
import com.easypark.app.core.presentation.composable.OwnerFooter
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.presentation.composable.ParkLoading
import com.easypark.app.core.presentation.composable.ParkTextField
import com.easypark.app.core.ui.ParkBackground
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReservationHistoryScreen(
    navController: NavHostController,
    viewModel: ReservationHistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = ParkBackground,
        topBar = {
            Column {
                ParkHeader(
                    title = "Reservas",
                    onNotificationClick = { navController.navigate(NavRoute.Notifications) }
                )
                
                // Buscador
                ParkTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = "Buscar por nombre..."
                )

                // Pestañas
                ReservationTabRow(
                    selectedTabIndex = state.selectedTab,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            }
        },
        bottomBar = {
            OwnerFooter(
                currentRoute = NavRoute.ReservationHistory,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(NavRoute.ReservationHistory) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            ParkLoading()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección Activas: Si hay reservas que terminan pronto, mostrar el separador
                val endingSoon = state.filteredReservations.filter { it.status == ReservationStatus.ENDING_SOON }
                val normallyActive = state.filteredReservations.filter { it.status == ReservationStatus.ACTIVE }
                
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(normallyActive) { reservation ->
                    ReservationCard(reservation = reservation)
                }

                if (endingSoon.isNotEmpty()) {
                    item {
                        Text(
                            text = "TERMINA PRONTO",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(endingSoon) { reservation ->
                        ReservationCard(reservation = reservation)
                    }
                }

                // Pestaña de Finalizadas (Tab index 1)
                if (state.selectedTab == 1) {
                    items(state.filteredReservations) { reservation ->
                        ReservationCard(reservation = reservation)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}