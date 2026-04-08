package com.easypark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.easypark.app.bookingconfirmation.presentation.screen.BookingConfirmationScreen
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.parkingdetails.presentation.screen.ParkingDetailsScreen
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.ParkingDetails("1")
    ) {
        composable<NavRoute.ParkingDetails> {
            val viewModel = koinViewModel<ParkingDetailsViewModel> {
                parametersOf("1") // ID de prueba
            }
            ParkingDetailsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBooking = { parkingId ->
                    navController.navigate(NavRoute.BookingConfirmation(parkingId))
                }
            )
        }

        composable<NavRoute.BookingConfirmation> {
            val viewModel = koinViewModel<BookingConfirmationViewModel> {
                parametersOf("1") // ID de prueba
            }
            BookingConfirmationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSuccess = { println("Navegar a éxito o historial") }
            )
        }
    }
}
