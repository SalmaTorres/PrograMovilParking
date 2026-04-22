package com.easypark.app.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.domain.session.SessionManager
import com.easypark.app.notifications.domain.usecase.GetNotificationsUseCase
import com.easypark.app.notifications.presentation.state.NotificationsEffect
import com.easypark.app.notifications.presentation.state.NotificationsEvent
import com.easypark.app.notifications.presentation.state.NotificationsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_notification
import kotlinproject.composeapp.generated.resources.ic_calendar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NotificationsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        startRealtimeNotifications()
    }

    fun onEvent(event: NotificationsEvent) {
        when (event) {
            NotificationsEvent.LoadNotifications -> startRealtimeNotifications()
            NotificationsEvent.OnBackClick -> emit(NotificationsEffect.NavigateBack)
        }
    }

    private fun startRealtimeNotifications() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getNotificationsUseCase.observe(userId).collect { newList ->
                _state.update { it.copy(
                    list = newList,
                    isLoading = false
                )}
                println("LOG: ¡Ha llegado una nueva notificación en tiempo real!")
            }
        }
    }

    private fun emit(effect: NotificationsEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}
