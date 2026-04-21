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

    init { loadNotifications() }

    fun onEvent(event: NotificationsEvent) {
        when (event) {
            NotificationsEvent.LoadNotifications -> loadNotifications()
            NotificationsEvent.OnBackClick -> emit(NotificationsEffect.NavigateBack)
        }
    }

    private fun loadNotifications() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val list = getNotificationsUseCase(userId)
                _state.update { it.copy(list = list, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun emit(effect: NotificationsEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}
