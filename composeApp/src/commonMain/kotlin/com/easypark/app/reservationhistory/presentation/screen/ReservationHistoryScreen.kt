package com.easypark.app.reservationhistory.presentation.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.reservationhistory.presentation.viewmodel.ReservationHistoryViewModel
import com.easypark.app.shared.presentation.composable.OwnerFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReservationHistoryScreen(
    navController: NavHostController,
    viewModel: ReservationHistoryViewModel = koinViewModel()
){
    Scaffold(
        topBar = {
            ParkHeader(
                title = "Historial de Reservas",
                onNotificationClick = {
                    navController.navigate(NavRoute.Notifications)
                }
            )
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
    ) {}
}