package com.easypark.app.bookingconfirmation.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easypark.app.shared.ui.ParkGray

@Composable
fun BookingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = ParkGray, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(2f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
    }
}