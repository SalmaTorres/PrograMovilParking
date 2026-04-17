package com.easypark.app.earnings.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.easypark.app.earnings.presentation.composable.EarningItemRow
import com.easypark.app.earnings.presentation.composable.EarningsStatCard
import com.easypark.app.earnings.presentation.composable.EarningsSummaryCard
import com.easypark.app.earnings.presentation.viewmodel.EarningsViewModel
import com.easypark.app.navigation.NavRoute
import com.easypark.app.core.presentation.composable.OwnerFooter
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.presentation.composable.ParkLoading
import com.easypark.app.core.ui.ParkBackground
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EarningsScreen(
    navController: NavHostController,
    viewModel: EarningsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = ParkBackground,
        topBar = {
            ParkHeader(
                title = "Downtown Plaza Garage", // variable que sera nombre del parqueo
                onNotificationClick = {
                    navController.navigate(NavRoute.Notifications)
                }
            )
        },
        bottomBar = {
            OwnerFooter(
                currentRoute = NavRoute.Earnings,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(NavRoute.Earnings) { saveState = true }
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
                item { Spacer(modifier = Modifier.height(8.dp)) }

                state.summary?.let { summary ->
                    item {
                        EarningsSummaryCard(
                            total = summary.totalEarnings,
                            percentage = summary.percentageChange
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                             EarningsStatCard(
                                 title = stringResource(Res.string.active_reservations),
                                 value = summary.activeReservations.toString(),
                                 subValue = stringResource(Res.string.earnings_vs_last_week, summary.reservationChange),
                                 icon = Res.drawable.ic_calendar,
                                 modifier = Modifier.weight(1f)
                            )
                            EarningsStatCard(
                                title = stringResource(Res.string.earnings_occupied_spaces),
                                value = summary.occupiedSpaces.toString(),
                                subValue = "/${summary.totalSpaces}",
                                icon = Res.drawable.ic_garage,
                                modifier = Modifier.weight(1f),
                                isAlert = summary.occupiedSpaces > summary.totalSpaces * 0.8
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(Res.string.earnings_recent_history),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(state.transactions) { transaction ->
                    EarningItemRow(transaction = transaction)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}