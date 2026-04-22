package com.easypark.app.notifications.data.repository

import com.easypark.app.core.data.remote.FirebaseManager
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import com.easypark.app.notifications.data.dto.NotificationDTO
import com.easypark.app.notifications.domain.model.NotificationModel
import com.easypark.app.notifications.domain.repository.NotificationsRepository
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_calendar
import kotlinproject.composeapp.generated.resources.ic_notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class NotificationsRepositoryImpl(
    private val localDS: NotificationLocalDataSource,
    private val firebaseManager: FirebaseManager
) : NotificationsRepository {

    override suspend fun observeNotificationsRealtime(userId: Int): Flow<List<NotificationModel>> {
        return firebaseManager.observeData("notifications/$userId").map { json ->
            if (json == null) return@map emptyList<NotificationModel>()

            try {
                val map = Json.decodeFromString<Map<String, NotificationDTO>>(json)

                map.values.map { dto ->
                    NotificationModel(
                        id = dto.id ?: 0,
                        title = dto.title ?: "",
                        description = dto.description ?: "",
                        time = dto.time ?: "Ahora",
                        icon = if (dto.title?.contains("Reserva") == true)
                            Res.drawable.ic_calendar else Res.drawable.ic_notification,
                        isUnread = dto.isUnread ?: true
                    )
                }.reversed()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getNotifications(userId: Int): List<NotificationModel> {
        val entities = localDS.readAll(userId)

        return entities.map { entity ->
            NotificationModel(
                id = entity.id,
                title = entity.title,
                description = entity.content,
                time = "Hace un momento",
                icon = if (entity.state == "RESERVATION") Res.drawable.ic_calendar else Res.drawable.ic_notification,
                isUnread = entity.state == "UNREAD"
            )
        }
    }
}