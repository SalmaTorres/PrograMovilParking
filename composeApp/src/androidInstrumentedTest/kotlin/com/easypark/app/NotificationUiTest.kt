package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easypark.app.notifications.domain.model.NotificationModel
import com.easypark.app.notifications.presentation.composable.NotificationItem
import org.junit.Assert.assertEquals
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_notification
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class NotificationUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun notificationItemDisplaysMainInformation() {
        setContent {
            NotificationItem(
                notification = notificationModel(),
                onClick = {}
            )
        }

        composeRule.onNodeWithText("Reserva confirmada").assertIsDisplayed()
        composeRule.onNodeWithText("Tu espacio fue reservado correctamente").assertIsDisplayed()
        composeRule.onNodeWithText("10:30").assertIsDisplayed()
    }

    @Test
    fun notificationItemInvokesClickWhenTapped() {
        var clicks = 0

        setContent {
            NotificationItem(
                notification = notificationModel(),
                onClick = { clicks++ }
            )
        }

        composeRule.onNodeWithText("Reserva confirmada").performClick()

        assertEquals(1, clicks)
    }

    private fun notificationModel(): NotificationModel {
        return NotificationModel(
            id = 1,
            title = "Reserva confirmada",
            description = "Tu espacio fue reservado correctamente",
            time = "10:30",
            icon = Res.drawable.ic_notification,
            isUnread = true
        )
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
