package com.easypark.app.bookingconfirmation.presentation.screen

import androidx.compose.runtime.Composable
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.bookingconfirmation.presentation.composable.BookingRow
import com.easypark.app.bookingconfirmation.presentation.composable.PaymentOption
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEffect
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.PaymentMethod
import com.easypark.app.navigation.NavRoute
import com.easypark.app.core.presentation.composable.ParkButton
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.ui.ParkBlue
import com.easypark.app.core.ui.ParkGray
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingConfirmationScreen(
    navController: NavHostController,
    viewModel: BookingConfirmationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BookingConfirmationEffect.NavigateBack -> navController.popBackStack()

                is BookingConfirmationEffect.NavigateToSuccess -> {
                    navController.navigate(NavRoute.ReservationSummary(effect.reservationId)) {

                        popUpTo(NavRoute.BookingConfirmation(state.bookingConfirmation?.parkingId?: 0)) {
                            inclusive = true
                        }
                    }
                }
                is BookingConfirmationEffect.ShowError -> { /* Mostrar un Toast o SnackBar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.booking_conf_title),
                onBackClick = { viewModel.onEvent(BookingConfirmationEvent.OnBackClick) },
                onNotificationClick = null
            )
        },
        bottomBar = {
            if (!state.isLoading && state.bookingConfirmation != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    ParkButton(
                        text = stringResource(Res.string.action_reserve),
                        onClick = { viewModel.onEvent(BookingConfirmationEvent.OnConfirmClick) }
                    )
                }
            }
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }  else if (state.bookingConfirmation != null) {
                val detail = state.bookingConfirmation!!

                val durationLabelText = if (detail.durationHours == 1) {
                    stringResource(Res.string.format_duration_singular, detail.durationHours)
                } else {
                    stringResource(Res.string.format_duration_plural, detail.durationHours)
                }

                Spacer(modifier = Modifier.height(16.dp))

                BookingRow(
                    label = stringResource(Res.string.label_location),
                    value = "${detail.locationName}, ${detail.address}"
                )
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                BookingRow(label = stringResource(Res.string.label_spaces), value = detail.spaceIdentifier)
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.label_duration),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(min = 80.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (detail.durationHours > 1) {
                                    viewModel.onEvent(BookingConfirmationEvent.OnDurationChange(detail.durationHours - 1))
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ParkBlue)
                        }
                        Text(
                            text = durationLabelText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = {
                                if (detail.durationHours < 24) {
                                    viewModel.onEvent(BookingConfirmationEvent.OnDurationChange(detail.durationHours + 1))
                                }
                            },
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ParkBlue)
                        }
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(Res.string.label_total_cost), color = ParkGray)
                    Text(
                        text = detail.totalCostText.format(),
                        color = ParkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.label_payment_method),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E0E0))
                        .padding(4.dp)
                ) {
                    PaymentOption(
                        text = stringResource(Res.string.payment_cash),
                        isSelected = state.selectedPaymentMethod == PaymentMethod.CASH,
                        onClick = { viewModel.onEvent(BookingConfirmationEvent.OnPaymentMethodSelected(PaymentMethod.CASH)) },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentOption(
                        text = stringResource(Res.string.payment_qr),
                        isSelected = state.selectedPaymentMethod == PaymentMethod.QR,
                        onClick = { viewModel.onEvent(BookingConfirmationEvent.OnPaymentMethodSelected(PaymentMethod.QR)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
