package com.easypark.app.reservationsummary.presentation.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.reservationsummary.presentation.viewmodel.ReservationSummaryViewModel
import com.easypark.app.shared.presentation.composable.DriverFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReservationSummaryScreen(
    navController: NavHostController,
    viewModel: ReservationSummaryViewModel = koinViewModel()
){
    Scaffold(
        topBar = {
            ParkHeader(
                title = "Informacion de Reserva",
                onNotificationClick = {
                    navController.navigate(NavRoute.Notifications)
                }
            )
        },
        bottomBar = {
            DriverFooter(
                currentRoute = NavRoute.ReservationSummary,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(NavRoute.ReservationSummary) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {}
}