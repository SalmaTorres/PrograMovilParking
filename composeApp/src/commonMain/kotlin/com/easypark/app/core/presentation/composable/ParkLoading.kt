package com.easypark.app.core.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.easypark.app.core.ui.ParkBlue
import com.easypark.app.core.ui.ParkGray
import org.jetbrains.compose.resources.painterResource

@Composable
fun ParkLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = ParkBlue)
    }
}

@Composable
fun ParkEmptyState(text: String, imageRes: org.jetbrains.compose.resources.DrawableResource) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(imageRes), null, Modifier.size(150.dp))
        Spacer(Modifier.height(16.dp))
        Text(text, textAlign = TextAlign.Center, color = ParkGray)
    }
}