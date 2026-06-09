package com.easypark.app.notifications.data.repository

import app.cash.turbine.test
import com.easypark.app.core.data.entity.NotificationEntity
import com.easypark.app.notifications.data.datasource.NotificationLocalDataSource
import com.easypark.app.notifications.domain.model.NotificationModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_calendar
import kotlinproject.composeapp.generated.resources.ic_notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class NotificationsRepositoryTest {

    @Test
    fun observeNotificationsRealtime_withJsonArray_mapsAndReversesSuccessfully() = runTest {
        val jsonArray = """
            [
                {
                    "id": 1,
                    "title": "Aviso General",
                    "message": "Tu saldo esta por vencer",
                    "time": "Hace 5 min",
                    "isUnread": true
                },
                {
                    "id": 2,
                    "title": "Reserva Confirmada",
                    "message": "Tu espacio esta listo",
                    "time": "Ahora",
                    "isUnread": false
                }
            ]
        """.trimIndent()

        val localDS = FakeNotificationLocalDataSource()
        val repository = NotificationsRepositoryImpl(
            localDS = localDS,
            observeRemoteData = { flowOf(jsonArray) }
        )

        repository.observeNotificationsRealtime(12).test {
            val result = awaitItem()
            assertEquals(2, result.size)

            // reversed() order -> id 2 comes first
            val first = result[0]
            assertEquals(2, first.id)
            assertEquals("Reserva Confirmada", first.title)
            assertEquals("Tu espacio esta listo", first.description)
            assertEquals("Ahora", first.time)
            assertEquals(Res.drawable.ic_calendar, first.icon)
            assertFalse(first.isUnread)

            // id 1 comes second
            val second = result[1]
            assertEquals(1, second.id)
            assertEquals("Aviso General", second.title)
            assertEquals("Tu saldo esta por vencer", second.description)
            assertEquals("Hace 5 min", second.time)
            assertEquals(Res.drawable.ic_notification, second.icon)
            assertTrue(second.isUnread)

            awaitComplete()
        }
    }

    @Test
    fun observeNotificationsRealtime_withJsonObjectMap_mapsAndReversesSuccessfully() = runTest {
        val jsonMap = """
            {
                "notif_1": {
                    "id": 10,
                    "title": "Notificacion 1",
                    "message": "Detalle 1",
                    "time": "Ayer",
                    "isUnread": true
                },
                "notif_2": {
                    "id": 20,
                    "title": "Reserva Cancelada",
                    "message": "Detalle 2",
                    "time": "Hoy",
                    "isUnread": false
                }
            }
        """.trimIndent()

        val localDS = FakeNotificationLocalDataSource()
        val repository = NotificationsRepositoryImpl(
            localDS = localDS,
            observeRemoteData = { flowOf(jsonMap) }
        )

        repository.observeNotificationsRealtime(12).test {
            val result = awaitItem()
            assertEquals(2, result.size)

            // The list maps values from Map/JsonObject and reverses it.
            // Map keys order isn't strictly defined, but usually matches declaration or hash.
            // Let's assert based on reversed values of dtoList.
            val ids = result.map { it.id }
            assertTrue(ids.contains(10))
            assertTrue(ids.contains(20))
            
            val reservaItem = result.first { it.id == 20 }
            assertEquals(Res.drawable.ic_calendar, reservaItem.icon)
            assertFalse(reservaItem.isUnread)

            val normalItem = result.first { it.id == 10 }
            assertEquals(Res.drawable.ic_notification, normalItem.icon)
            assertTrue(normalItem.isUnread)

            awaitComplete()
        }
    }

    @Test
    fun observeNotificationsRealtime_withNullOrMalformedJson_returnsEmptyList() = runTest {
        val localDS = FakeNotificationLocalDataSource()
        
        // Null payload
        val repoWithNull = NotificationsRepositoryImpl(
            localDS = localDS,
            observeRemoteData = { flowOf(null) }
        )
        repoWithNull.observeNotificationsRealtime(12).test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }

        // Malformed payload
        val repoWithMalformed = NotificationsRepositoryImpl(
            localDS = localDS,
            observeRemoteData = { flowOf("{invalid-json") }
        )
        repoWithMalformed.observeNotificationsRealtime(12).test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun getNotifications_mapsLocalEntitiesCorrectly() = runTest {
        val localDS = FakeNotificationLocalDataSource().apply {
            listResult = listOf(
                NotificationEntity(
                    userId = 12,
                    title = "Titulo Local 1",
                    content = "Contenido Local 1",
                    state = "UNREAD"
                ).apply { id = 101 },
                NotificationEntity(
                    userId = 12,
                    title = "Reserva Local 2",
                    content = "Contenido Local 2",
                    state = "RESERVATION"
                ).apply { id = 102 }
            )
        }

        val repository = NotificationsRepositoryImpl(
            localDS = localDS,
            observeRemoteData = { flowOf(null) }
        )

        val result = repository.getNotifications(12)

        assertEquals(12, localDS.readAllUserId)
        assertEquals(2, result.size)

        val first = result[0]
        assertEquals(101, first.id)
        assertEquals("Titulo Local 1", first.title)
        assertEquals("Contenido Local 1", first.description)
        assertTrue(first.isUnread)
        assertEquals(Res.drawable.ic_notification, first.icon)

        val second = result[1]
        assertEquals(102, second.id)
        assertEquals("Reserva Local 2", second.title)
        assertEquals("Contenido Local 2", second.description)
        assertFalse(second.isUnread)
        assertEquals(Res.drawable.ic_calendar, second.icon)
    }

    private class FakeNotificationLocalDataSource : NotificationLocalDataSource {
        val createdEntities = mutableListOf<NotificationEntity>()
        var readAllUserId: Int? = null
        var listResult: List<NotificationEntity> = emptyList()

        override suspend fun create(entity: NotificationEntity) {
            createdEntities += entity
        }

        override suspend fun readById(id: Int): NotificationEntity? = null

        override suspend fun readAll(userId: Int): List<NotificationEntity> {
            readAllUserId = userId
            return listResult
        }

        override suspend fun updateStatus(id: Int, isViewed: Boolean): Boolean = false
        override suspend fun deleteById(id: Int) {}
        override suspend fun deleteAll(userId: Int) {}
    }
}
