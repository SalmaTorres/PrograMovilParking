package com.easypark.app.spacemanagement.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easypark.app.shared.presentation.composable.OwnerFooter
import com.easypark.app.shared.presentation.composable.ParkHeader
import com.easypark.app.spacemanagement.domain.model.ParkingSpot
import com.easypark.app.spacemanagement.domain.model.SpaceSummary
import com.easypark.app.spacemanagement.presentation.viewmodel.SpaceManagementViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpaceManagementScreen(
    viewModel: SpaceManagementViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            ParkHeader(
                title = "Gestión de Espacios",
                onBackClick = onNavigateBack,
                onNotificationClick = null
            )
        },
        bottomBar = {
            OwnerFooter(currentScreen = "espacios_owner", onNavigate = onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.errorMessage ?: "", color = Color.Red)
                }
            } else {
                state.summary?.let { summary ->
                    Spacer(modifier = Modifier.height(16.dp))
                    SummaryCard(summary = summary)
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Parking Spots",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.parkingSpots) { spot ->
                        ParkingSpotItem(spot = spot)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(summary: SpaceSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3CC9)) // Azul del diseño
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "CAPACIDAD TOTAL",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "${summary.totalCapacity}",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " Espacios",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column {
                    Text(
                        text = "Ocupados",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${summary.occupied}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.height(36.dp).width(1.dp)
                )
                
                Column {
                    Text(
                        text = "Disponibles",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${summary.available}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ParkingSpotItem(spot: ParkingSpot) {
    val borderColor = if (spot.isOccupied) Color(0xFFFCA5A5) else Color(0xFF86EFAC)
    val bgColor = if (spot.isOccupied) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
    val textColor = if (spot.isOccupied) Color(0xFFDC2626) else Color(0xFF16A34A)
    val statusText = if (spot.isOccupied) "OCUPADO" else "LIBRE"

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(color = bgColor, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = spot.code,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
