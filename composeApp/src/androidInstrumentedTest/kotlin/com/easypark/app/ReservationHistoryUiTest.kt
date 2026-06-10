package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easypark.app.reservationhistory.domain.model.ReservationItemModel
import com.easypark.app.reservationhistory.presentation.composable.ReservationCard
import com.easypark.app.reservationhistory.presentation.composable.ReservationTabRow
import com.easypark.app.core.domain.model.status.ReservationStatus
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class ReservationHistoryUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun reservationTabRow_displaysTabsAndInvokesClick() {
        var selectedIndex = 0
        setContent {
            ReservationTabRow(
                selectedTabIndex = selectedIndex,
                onTabSelected = { selectedIndex = it }
            )
        }

        // The tab names should match the resource strings or displays
        composeRule.onNodeWithText("Activas").assertIsDisplayed()
        composeRule.onNodeWithText("Historial").assertIsDisplayed().performClick()
        
        assertEquals(1, selectedIndex)
    }

    @Test
    fun reservationCard_displaysMainInformation() {
        setContent {
            ReservationCard(
                reservation = reservationModel(ReservationStatus.ACTIVE),
                onCheckInClick = {}
            )
        }

        composeRule.onNodeWithText("Carlos Gomez").assertIsDisplayed()
        composeRule.onNodeWithText("10:00 - 12:00").assertIsDisplayed()
        composeRule.onNodeWithText("ABC-123 • Automóvil").assertIsDisplayed()
    }

    @Test
    fun reservationCard_showsCheckInButtonOnlyWhenPending() {
        var checkInClicked = false
        setContent {
            ReservationCard(
                reservation = reservationModel(ReservationStatus.PENDIENTE),
                onCheckInClick = { checkInClicked = true }
            )
        }

        // Button should be visible and clickable
        composeRule.onNodeWithText("Marcar Llegada (Check-in)").assertIsDisplayed().performClick()
        assertEquals(true, checkInClicked)
    }

    @Test
    fun reservationCard_hidesCheckInButtonWhenActive() {
        setContent {
            ReservationCard(
                reservation = reservationModel(ReservationStatus.ACTIVE),
                onCheckInClick = {}
            )
        }

        // Button should not exist
        composeRule.onNodeWithText("Marcar Llegada (Check-in)").assertDoesNotExist()
    }

    private fun reservationModel(status: ReservationStatus): ReservationItemModel {
        return ReservationItemModel(
            id = 101,
            clientName = "Carlos Gomez",
            spaceLabel = "A-12",
            startTime = "10:00",
            endTime = "12:00",
            status = status,
            vehiclePlate = "ABC-123",
            vehicleType = "Automóvil"
        )
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
