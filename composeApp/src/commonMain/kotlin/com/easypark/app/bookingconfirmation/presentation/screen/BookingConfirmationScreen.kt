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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.bookingconfirmation.presentation.composable.BookingRow
import com.easypark.app.bookingconfirmation.presentation.composable.PaymentOption
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEffect
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.PaymentMethod
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkHeader
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingConfirmationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: BookingConfirmationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BookingConfirmationEffect.NavigateBack -> onNavigateBack()
                BookingConfirmationEffect.NavigateToSuccess -> {
                    onNavigateToSuccess()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.booking_confirmation_title),
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
                        text = stringResource(Res.string.confirm_booking_button),
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

                Spacer(modifier = Modifier.height(16.dp))

                BookingRow(
                    label = stringResource(Res.string.location_label),
                    value = "${detail.locationName}, ${detail.address}"
                )
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                BookingRow(label = stringResource(Res.string.space_label), value = detail.spaceIdentifier)
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(Res.string.duration_label), color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (detail.durationHours > 1) {
                                    viewModel.onEvent(BookingConfirmationEvent.OnDurationChange(detail.durationHours - 1))
                                }
                            },
                        ) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
                        }
                        Text(
                            text = detail.durationText,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = {
                                if (detail.durationHours < 24) {
                                    viewModel.onEvent(BookingConfirmationEvent.OnDurationChange(detail.durationHours + 1))
                                }
                            },
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
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
                    Text(text = stringResource(Res.string.total_cost_label), color = Color.Gray)
                    Text(text = detail.totalCostText, color = Color.Blue, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.payment_method_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0E0E0))
                        .padding(4.dp)
                ) {
                    PaymentOption(
                        text = stringResource(Res.string.cash_payment),
                        isSelected = state.selectedPaymentMethod == PaymentMethod.CASH,
                        onClick = { viewModel.onEvent(BookingConfirmationEvent.OnPaymentMethodSelected(PaymentMethod.CASH)) },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentOption(
                        text = stringResource(Res.string.qr_payment),
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