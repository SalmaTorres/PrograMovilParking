package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easypark.app.core.presentation.composable.ParkEmptyState
import com.easypark.app.core.presentation.composable.ParkHeader
import com.easypark.app.core.presentation.composable.ParkLoading
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_garage
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class RegisterParkingUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun parkHeader_displaysTitle() {
        setContent {
            ParkHeader(
                title = "Registrar Parqueo"
            )
        }

        composeRule.onNodeWithText("Registrar Parqueo").assertIsDisplayed()
    }

    @Test
    fun parkHeader_displaysLogoutAndInvokesCallback() {
        var logoutClicked = false
        setContent {
            ParkHeader(
                title = "Mi Parqueo",
                onLogoutClick = { logoutClicked = true }
            )
        }

        composeRule.onNodeWithContentDescription("Cerrar Sesión").assertIsDisplayed().performClick()
        assertEquals(true, logoutClicked)
    }

    @Test
    fun parkEmptyState_displaysMessage() {
        setContent {
            ParkEmptyState(
                text = "No hay parqueos registrados",
                imageRes = Res.drawable.ic_garage
            )
        }

        composeRule.onNodeWithText("No hay parqueos registrados").assertIsDisplayed()
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
