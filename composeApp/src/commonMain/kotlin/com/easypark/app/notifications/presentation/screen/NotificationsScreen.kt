package com.easypark.app.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easypark.app.notifications.presentation.composable.NotificationItem
import com.easypark.app.notifications.presentation.state.NotificationsEffect
import com.easypark.app.notifications.presentation.state.NotificationsEvent
import com.easypark.app.notifications.presentation.viewmodel.NotificationsViewModel
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.ui.ParkGray
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.notifications_empty
import kotlinproject.composeapp.generated.resources.notifications_title
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NotificationsEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            ParkHeader(
                title = stringResource(Res.string.notifications_title),
                onBackClick = { viewModel.onEvent(NotificationsEvent.OnBackClick) },
                onNotificationClick = null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (state.list.isEmpty() && !state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.notifications_empty),
                        color = ParkGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.list.size) { index ->
                        val notification = state.list[index]
                        NotificationItem(
                            notification = notification,
                            onClick = { /* Acción al hacer click */ }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = ParkGray.copy(alpha = 0.2f)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = stringResource(Res.string.notifications_empty),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                            textAlign = TextAlign.Center,
                            color = ParkGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}