package com.easypark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.easypark.app.bookingconfirmation.presentation.screen.BookingConfirmationScreen
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.earnings.presentation.screen.EarningsScreen
import com.easypark.app.findparking.presentation.screen.FindParkingScreen
import com.easypark.app.notifications.presentation.screen.NotificationsScreen
import com.easypark.app.parkingdetails.presentation.screen.ParkingDetailsScreen
import com.easypark.app.parkingdetails.presentation.viewmodel.ParkingDetailsViewModel
import com.easypark.app.register.presentation.screen.RegisterScreen
import com.easypark.app.registerparking.presentation.screen.RegisterParkingScreen
import com.easypark.app.registervehicle.presentation.screen.RegisterVehicleScreen
import com.easypark.app.reservationhistory.presentation.screen.ReservationHistoryScreen
import com.easypark.app.reservationsummary.presentation.screen.ReservationSummaryScreen
import com.easypark.app.signin.presentation.screen.SignInScreen
import com.easypark.app.spacemanagement.presentation.screen.SpaceManagementScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

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

        composable<NavRoute.RegisterParking>(
            typeMap = mapOf(typeOf<UserModel>() to UserModelType) // <--- ESTO ES LO QUE FALTA
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.RegisterParking>()
            RegisterParkingScreen(
                navController = navController,
                userFromStep1 = args.userFromStep1
            )
        }

        composable<NavRoute.RegisterVehicle>(
            typeMap = mapOf(typeOf<UserModel>() to UserModelType)
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.RegisterVehicle>()
            RegisterVehicleScreen(
                navController = navController,
                userFromStep1 = args.userFromStep1
            )
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
            ReservationSummaryScreen(navController = navController)
        }

        composable<NavRoute.ParkingDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.ParkingDetails>()

            val viewModel = koinViewModel<ParkingDetailsViewModel> {
                parametersOf(args.id)
            }

            ParkingDetailsScreen(
                parkingId = args.id,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBooking = { idDeRegreso ->
                    navController.navigate(NavRoute.BookingConfirmation(id = idDeRegreso.toInt()))
                }
            )
        }

        composable<NavRoute.BookingConfirmation> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.BookingConfirmation>()

            BookingConfirmationScreen(
                parkingId = args.id,
                navController = navController
            )
        }
    }
}
