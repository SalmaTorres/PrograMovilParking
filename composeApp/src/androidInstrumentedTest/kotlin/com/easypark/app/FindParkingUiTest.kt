package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.findparking.presentation.composable.ParkingDetailCard
import com.easypark.app.registerparking.domain.model.ParkingModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class FindParkingUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun parkingDetailCardShowsParkingDataAndAllowsActions() {
        var reserveClicks = 0
        var detailsClicks = 0
        val parking = parkingModel(isAvailable = true)

        setContent {
            ParkingDetailCard(
                parking = parking,
                onReserve = { reserveClicks++ },
                onDetails = { detailsClicks++ },
                onClose = {}
            )
        }

        composeRule.onNodeWithText("Parqueo Central").assertIsDisplayed()
        composeRule.onNodeWithText("8.50 Bs / h").assertIsDisplayed()
        composeRule.onNodeWithText("Disponible").assertIsDisplayed()
        composeRule.onNodeWithText("Reservar").assertIsEnabled().performClick()
        composeRule.onNodeWithText("Ver M\u00e1s").assertHasClickAction().performClick()

        assertEquals(1, reserveClicks)
        assertEquals(1, detailsClicks)
    }

    @Test
    fun parkingDetailCardDisablesReservationWhenParkingIsFull() {
        val parking = parkingModel(isAvailable = false)

        setContent {
            ParkingDetailCard(
                parking = parking,
                onReserve = {},
                onDetails = {},
                onClose = {}
            )
        }

        composeRule.onNodeWithText("Lleno").assertIsDisplayed()
        composeRule.onNodeWithText("Reservar").assertIsNotEnabled()
    }

    private fun parkingModel(isAvailable: Boolean): ParkingModel {
        return ParkingModel(
            id = 7,
            name = "Parqueo Central",
            address = "Av. Siempre Viva",
            latitude = -17.3895,
            longitude = -66.1568,
            pricePerHour = PriceModel(amount = 8.50),
            rating = 4.5f,
            totalSpaces = 20,
            availableSpaces = if (isAvailable) 5 else 0,
            isAvailable = isAvailable,
            ownerId = 3
        )
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
