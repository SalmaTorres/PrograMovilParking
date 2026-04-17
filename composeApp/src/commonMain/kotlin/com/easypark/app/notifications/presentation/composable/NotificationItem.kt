package com.easypark.app.notifications.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.notifications.domain.model.NotificationModel
import com.easypark.app.core.ui.ParkBlueLight
import com.easypark.app.core.ui.ParkGray
import com.easypark.app.core.ui.ParkTextDark
import org.jetbrains.compose.resources.painterResource

@Composable
fun NotificationItem(
    notification: NotificationModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ParkBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(notification.icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (notification.isUnread) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(ParkTextDark)
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ParkTextDark,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.description,
                fontSize = 13.sp,
                color = ParkGray,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Time and Arrow
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = notification.time,
                fontSize = 12.sp,
                color = ParkGray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(">", color = ParkGray, fontSize = 12.sp)
        }
    }
}