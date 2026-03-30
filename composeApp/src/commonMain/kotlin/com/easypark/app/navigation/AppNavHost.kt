package com.easypark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Imports from user's snippet
// import com.ucb.app.github.presentation.screen.GithubScreen 
// import com.ucb.app.movie.presentation.screen.MovieScreen

// Our newly created screens
import com.easypark.app.bookingconfirmation.presentation.screen.BookingConfirmationScreen
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.parkingdetails.presentation.screen.ParkingDetailsScreen
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.ParkingDetails) {



        composable<NavRoute.ParkingDetails> {
            val viewModel = koinViewModel<ParkingDetailsViewModel>()
            ParkingDetailsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
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
