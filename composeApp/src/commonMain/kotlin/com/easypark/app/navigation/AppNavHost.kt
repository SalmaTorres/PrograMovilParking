package com.easypark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.easypark.app.earnings.presentation.screen.EarningsScreen
import com.easypark.app.notifications.presentation.screen.NotificationsScreen
import com.easypark.app.register.presentation.screen.RegisterScreen
import com.easypark.app.registerparking.presentation.screen.RegisterParkingScreen
import com.easypark.app.reservationhistory.presentation.screen.ReservationHistoryScreen
import com.easypark.app.signin.presentation.screen.SignInScreen
import com.easypark.app.spacemanagement.presentation.screen.SpaceManagementScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.SignIn
    ) {
        composable<NavRoute.SignIn> {
            SignInScreen(navController)
        }

        composable<NavRoute.Register> {
            RegisterScreen(navController)
        }

        composable<NavRoute.RegisterParking> {
            RegisterParkingScreen(navController)
        }

        composable<NavRoute.SpaceManagement> {
            SpaceManagementScreen(navController)
        }

        composable<NavRoute.Notifications> {
            NotificationsScreen(navController)
        }

        composable<NavRoute.Earnings> {
            EarningsScreen(navController)
        }

        composable<NavRoute.ReservationHistory> {
            ReservationHistoryScreen(navController)
        }


//        composable<NavRoute.ParkingDetails> {
//            val viewModel = koinViewModel<ParkingDetailsViewModel>()
//            ParkingDetailsScreen(
//                viewModel = viewModel,
//                onNavigateBack = { navController.popBackStack() },
//                onNavigateToBooking = { parkingId ->
//                    navController.navigate(NavRoute.BookingConfirmation)
//                }
//            )
//        }
        
//        composable<NavRoute.BookingConfirmation> {
//            val viewModel = koinViewModel<BookingConfirmationViewModel>()
//            BookingConfirmationScreen(
//                viewModel = viewModel,
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
    }
}
