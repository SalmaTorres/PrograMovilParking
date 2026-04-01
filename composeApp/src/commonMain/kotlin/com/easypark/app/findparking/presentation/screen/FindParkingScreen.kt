package com.easypark.app.findparking.presentation.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.easypark.app.findparking.presentation.viewmodel.FindParkingViewModel
import com.easypark.app.navigation.NavRoute
import com.easypark.app.shared.presentation.composable.DriverFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FindParkingScreen(
    navController: NavHostController,
    viewModel: FindParkingViewModel = koinViewModel()
){
    Scaffold(
        topBar = {
            ParkHeader(
                title = "Mapa",
                onNotificationClick = {
                    navController.navigate(NavRoute.Notifications)
                }
            )
        },
        bottomBar = {
            DriverFooter(
                currentRoute = NavRoute.Earnings,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(NavRoute.FindParking) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {}
}