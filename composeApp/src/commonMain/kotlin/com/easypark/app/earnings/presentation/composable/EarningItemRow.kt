package com.easypark.app.earnings.presentation.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.earnings.domain.model.EarningTransaction
import com.easypark.app.core.ui.ParkGray
import com.easypark.app.core.ui.ParkSuccess
import com.easypark.app.core.ui.ParkTextDark

@Composable
fun EarningItemRow(
    transaction: EarningTransaction,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.date.uppercase(),
                    color = ParkGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.label,
                    color = ParkTextDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "${transaction.currency} ${transaction.amount.toInt()}.00",
                color = ParkSuccess,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
