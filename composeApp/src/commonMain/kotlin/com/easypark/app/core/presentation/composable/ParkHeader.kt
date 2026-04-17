package com.easypark.app.core.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.easypark.app.core.ui.*
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ParkHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null
) {
    Surface(color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp)) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Image(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.back),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ParkTextDark
            )

            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                if (onNotificationClick != null) {
                    Surface(
                        modifier = Modifier.size(40.dp).clickable { onNotificationClick() },
                        shape = CircleShape,
                        color = ParkBlueLight
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_notification),
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}
