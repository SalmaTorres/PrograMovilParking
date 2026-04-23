package com.easypark.app.core.data.repository

import com.easypark.app.core.data.dao.AppEventDao
import com.easypark.app.core.data.entity.AppEventEntity
import com.easypark.app.core.domain.repository.AppEventRepository

class AppEventRepositoryImpl(
    private val dao: AppEventDao
) : AppEventRepository {
    override suspend fun logEvent(eventType: String, timestamp: Long) {
        val event = AppEventEntity(
            eventType = eventType,
            timestamp = timestamp,
            isSynced = false
        )
        dao.insertEvent(event)
    }

    override suspend fun getUnsyncedEvents(): List<AppEventEntity> {
        return dao.getUnsyncedEvents()
    }

    override suspend fun markAsSynced(events: List<AppEventEntity>) {
        val syncedEvents = events.map { it.copy(isSynced = true) }
        dao.updateEvents(syncedEvents)
    }
}
