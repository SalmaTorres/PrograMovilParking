package com.easypark.app.spacemanagement.presentation.viewmodel

sealed interface SpaceManagementEvent {
    object LoadData : SpaceManagementEvent
}
