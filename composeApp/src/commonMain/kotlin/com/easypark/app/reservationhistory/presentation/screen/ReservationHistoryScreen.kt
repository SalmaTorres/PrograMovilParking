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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.reservationhistory.domain.model.ReservationStatus
import com.easypark.app.reservationhistory.presentation.composable.ReservationCard
import com.easypark.app.reservationhistory.presentation.composable.ReservationTabRow
import com.easypark.app.reservationhistory.presentation.viewmodel.ReservationHistoryViewModel
import com.easypark.app.shared.presentation.composable.OwnerFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import com.easypark.app.shared.presentation.composable.ParkLoading
import com.easypark.app.shared.presentation.composable.ParkTextField
import com.easypark.app.shared.ui.ParkBackground
import com.easypark.app.shared.ui.ParkGray
import org.koin.compose.viewmodel.koinViewModel
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReservationHistoryScreen(
    navController: NavHostController,
    viewModel: ReservationHistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val filteredReservations = viewModel.getFilteredReservations()

    Scaffold(
        containerColor = ParkBackground,
        topBar = {
            Column {
                ParkHeader(
                    title = stringResource(Res.string.history_title),
                    onNotificationClick = { navController.navigate(NavRoute.Notifications) }
                )

                ParkTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = stringResource(Res.string.hint_search),
                )

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
                val endingSoon = filteredReservations.filter { it.status == ReservationStatus.ENDING_SOON }
                val normallyActive = filteredReservations.filter { it.status == ReservationStatus.ACTIVE }
                
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(normallyActive) { reservation ->
                    ReservationCard(reservation = reservation)
                }

                if (endingSoon.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.history_status_ending_soon),
                            color = ParkGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(endingSoon) { reservation ->
                        ReservationCard(reservation = reservation)
                    }
                }

                if (state.selectedTab == 1) {
                    items(filteredReservations) { reservation ->
                        ReservationCard(reservation = reservation)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}