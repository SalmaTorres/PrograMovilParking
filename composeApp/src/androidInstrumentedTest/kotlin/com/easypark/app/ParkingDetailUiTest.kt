package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.easypark.app.parkingdetails.presentation.composable.DetailItem
import org.junit.Rule
import org.junit.Test

class ParkingDetailUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun detailItemShowsAddressInformation() {
        setContent {
            DetailItem(
                icon = Icons.Default.LocationOn,
                label = "Direccion",
                text = "Av. America #123"
            )
        }

        composeRule.onNodeWithText("Direccion").assertIsDisplayed()
        composeRule.onNodeWithText("Av. America #123").assertIsDisplayed()
    }

    @Test
    fun detailItemsShowPriceAndScheduleInformation() {
        setContent {
            Column {
                DetailItem(
                    icon = Icons.Default.Payments,
                    label = "Precio/hora",
                    text = "8.50 Bs"
                )
                DetailItem(
                    icon = Icons.Default.AccessTime,
                    label = "Horario",
                    text = "08:00 - 22:00"
                )
            }
        }

        composeRule.onNodeWithText("Precio/hora").assertIsDisplayed()
        composeRule.onNodeWithText("8.50 Bs").assertIsDisplayed()
        composeRule.onNodeWithText("Horario").assertIsDisplayed()
        composeRule.onNodeWithText("08:00 - 22:00").assertIsDisplayed()
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}

