package com.easypark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.easypark.app.bookingconfirmation.presentation.screen.BookingConfirmationScreen
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.notifications.presentation.screen.NotificationsScreen
import com.easypark.app.notifications.presentation.viewmodel.NotificationsViewModel
import com.easypark.app.parkingdetails.presentation.screen.ParkingDetailsScreen
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.ParkingDetails
    ) {

        composable<NavRoute.Notifications> {
            val viewModel = koinViewModel<NotificationsViewModel>()
            NotificationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<NavRoute.ParkingDetails> {
            val viewModel = koinViewModel<ParkingDetailsViewModel>()
            ParkingDetailsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBooking = { parkingId ->
                    navController.navigate(NavRoute.BookingConfirmation)
                }
            )
        }
        
        composable<NavRoute.BookingConfirmation> {
            val viewModel = koinViewModel<BookingConfirmationViewModel>()
            BookingConfirmationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
