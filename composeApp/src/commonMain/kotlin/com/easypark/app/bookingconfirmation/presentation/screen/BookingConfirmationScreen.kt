package com.easypark.app.bookingconfirmation.presentation.screen

import androidx.compose.runtime.Composable
import com.easypark.app.bookingconfirmation.presentation.viewmodel.BookingConfirmationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.bookingconfirmation.presentation.state.BookingConfirmationEvent
import com.easypark.app.bookingconfirmation.presentation.state.PaymentMethod
import com.easypark.app.shared.presentation.composable.ParkButton
import com.easypark.app.shared.presentation.composable.ParkHeader
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookingConfirmationScreen(
    viewModel: BookingConfirmationViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.booking_confirmation_title),
                onBackClick = onNavigateBack,
                onNotificationClick = null
            )
        },
        bottomBar = {
            if (!state.isLoading && state.bookingConfirmation != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ParkButton(
                        text = stringResource(Res.string.confirm_booking_button),
                        onClick = { viewModel.onEvent(BookingConfirmationEvent.OnConfirmClick) },
                        isSecondary = false
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.bookingConfirmation != null) {
                val detail = state.bookingConfirmation!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    BookingRow(label = stringResource(Res.string.location_label), value = "${detail.locationName}, ${detail.address}")
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    BookingRow(label = stringResource(Res.string.space_label), value = detail.spaceIdentifier)
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    BookingRow(label = stringResource(Res.string.duration_label), value = detail.durationText)
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

                    // Segmented Control
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E0E0)) // Light gray background
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
                }
            }
        }
    }
}

@Composable
fun BookingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(2f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
    }
}

@Composable
fun PaymentOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Blue else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
