package com.easypark.app.core.domain.repository

import com.easypark.app.core.data.entity.AppEventEntity

interface AppEventRepository {
    suspend fun logEvent(eventType: String, timestamp: Long)
    suspend fun getUnsyncedEvents(): List<AppEventEntity>
    suspend fun markAsSynced(events: List<AppEventEntity>)
}
