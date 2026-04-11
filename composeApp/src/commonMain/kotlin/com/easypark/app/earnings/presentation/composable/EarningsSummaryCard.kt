package com.easypark.app.earnings.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.shared.ui.ParkBlue
import com.easypark.app.shared.ui.ParkBlueLight
import com.easypark.app.shared.ui.ParkGray
import com.easypark.app.shared.ui.ParkSuccess
import com.easypark.app.shared.ui.ParkTextDark
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.earnings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EarningsSummaryCard(
    total: Double,
    percentage: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.earnings_title),
                    color = ParkGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(ParkBlueLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+$percentage%",
                        color = ParkSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${formatAmount(total)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ParkTextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                LinearProgressIndicator(
                    progress = { 0.7f }, // Valor fijo para el diseño visual
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = ParkBlue,
                    trackColor = Color(0xFFF0F0F0),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return amount.toString().replace(".0", ".00") // Simplificación para el mock
}
