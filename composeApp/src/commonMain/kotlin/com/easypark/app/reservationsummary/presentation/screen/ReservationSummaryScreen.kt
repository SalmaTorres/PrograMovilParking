package com.easypark.app.reservationsummary.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.easypark.app.navigation.NavRoute
import com.easypark.app.reservationsummary.presentation.viewmodel.ReservationSummaryViewModel
import com.easypark.app.core.presentation.composable.DriverFooter
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.ui.ParkBlue
import com.easypark.app.core.ui.ParkBackground
import com.easypark.app.core.ui.ParkGray
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.easypark.app.reservationsummary.presentation.composable.DetailRow
import org.koin.core.parameter.parametersOf

@Composable
fun ReservationSummaryScreen(
    reservationId: NavHostController,
    navController: NavController? = null,
    viewModel: ReservationSummaryViewModel = koinViewModel { parametersOf(reservationId) }
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.reservation_summary_title),
                onBackClick = { navController?.popBackStack() },
                onNotificationClick = null
            )
        },
        bottomBar = {
            DriverFooter(
                currentRoute = NavRoute.ReservationSummary(0),
                onNavigate = { route ->
                    if (route !is NavRoute.ReservationSummary) {
                        navController?.navigate(route)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.reservation?.let { reservation ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ParkBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(Res.string.reservation_summary_location), color = ParkGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.parkingName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(reservation.address, color = ParkGray, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow(Icons.Default.Place, stringResource(Res.string.reservation_summary_space), "Nro ${reservation.spaceNumber}")
                        DetailRow(Icons.Default.Refresh, stringResource(Res.string.reservation_summary_entry_time), reservation.entryTime)
                        DetailRow(Icons.Default.Build, stringResource(Res.string.reservation_summary_location), reservation.durationText)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBox, contentDescription = null, tint = ParkBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(Res.string.reservation_summary_cost), fontWeight = FontWeight.Medium)
                            }
                            Text("Bs ${reservation.totalPrice}", color = ParkBlue, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(Res.string.reservation_summary_payment), fontWeight = FontWeight.Medium)
                            }
                            Text(reservation.paymentMethod, color = ParkGray)
                        }
                    }
                }
            }
        }
    }
}