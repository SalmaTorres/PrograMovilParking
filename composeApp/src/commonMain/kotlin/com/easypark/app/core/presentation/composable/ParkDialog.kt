package com.easypark.app.core.presentation.composable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easypark.app.core.ui.ParkBlue
import com.easypark.app.core.ui.ParkGray

@Composable
fun ParkDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirmar", color = ParkBlue) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = ParkGray) }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}