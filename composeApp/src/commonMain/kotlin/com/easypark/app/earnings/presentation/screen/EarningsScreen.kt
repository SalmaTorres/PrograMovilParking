package com.easypark.app.earnings.presentation.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.easypark.app.earnings.presentation.viewmodel.EarningsViewModel
import com.easypark.app.navigation.NavRoute
import com.easypark.app.shared.presentation.composable.OwnerFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EarningsScreen(
    navController: NavHostController,
    viewModel: EarningsViewModel = koinViewModel()
){
    Scaffold(
        topBar = {
            ParkHeader(
                title = "Nombre del parqueo",
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
    ) {}
}