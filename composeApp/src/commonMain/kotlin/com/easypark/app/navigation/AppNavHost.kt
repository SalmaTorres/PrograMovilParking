package com.easypark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.easypark.app.bookingconfirmation.presentation.screen.BookingConfirmationScreen
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.earnings.presentation.screen.EarningsScreen
import com.easypark.app.findparking.presentation.screen.FindParkingScreen
import com.easypark.app.notifications.presentation.screen.NotificationsScreen
import com.easypark.app.parkingdetails.presentation.screen.ParkingDetailsScreen
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import com.easypark.app.register.presentation.screen.RegisterScreen
import com.easypark.app.registerparking.presentation.screen.RegisterParkingScreen
import com.easypark.app.reservationhistory.presentation.screen.ReservationHistoryScreen
import com.easypark.app.reservationsummary.presentation.screen.ReservationSummaryScreen
import com.easypark.app.signin.presentation.screen.SignInScreen
import com.easypark.app.spacemanagement.presentation.screen.SpaceManagementScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

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

        composable<NavRoute.FindParking> {
            FindParkingScreen(navController)
        }

        composable<NavRoute.ReservationSummary> {
            ReservationSummaryScreen(navController)
        }

        composable<NavRoute.ParkingDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.ParkingDetails>()
            val viewModel: ParkingDetailsViewModel = koinViewModel {
                parametersOf(args.id)
            }

            ParkingDetailsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable<NavRoute.BookingConfirmation> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.BookingConfirmation>()
            val viewModel: BookingConfirmationViewModel = koinViewModel {
                parametersOf(args.id)
            }

            BookingConfirmationScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
