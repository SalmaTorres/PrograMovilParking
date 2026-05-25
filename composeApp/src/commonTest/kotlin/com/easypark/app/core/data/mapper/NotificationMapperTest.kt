package com.easypark.app.core.data.mapper

import com.easypark.app.notifications.data.dto.NotificationDTO
import com.easypark.app.notifications.domain.model.NotificationModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_notification

class NotificationMapperTest {
    @Test
    fun notificationRoundTripPersistsUnreadState() {
        val source = NotificationModel(
            id = 14,
            title = "Reserva",
            description = "Tu espacio esta reservado",
            time = "Ahora",
            icon = Res.drawable.ic_notification,
            isUnread = true
        )

        val result = source.toEntity(userId = 8).toModel()

        assertEquals(source.id, result.id)
        assertEquals(source.title, result.title)
        assertEquals(source.description, result.description)
        assertTrue(result.isUnread)
    }

    @Test
    fun firebaseNotificationMapsMessageAndReadDefault() {
        val mapped = NotificationDTO(
            id = 2,
            title = "Aviso",
            description = "Mensaje",
            time = "Ahora",
            isUnread = false
        ).toDomain()
        val empty = NotificationDTO(id = null, title = null, description = null, isUnread = null).toDomain()

        assertEquals("Mensaje", mapped.description)
        assertFalse(mapped.isUnread)
        assertEquals(0, empty.id)
        assertTrue(empty.isUnread)
    }
}
