package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easypark.app.core.domain.model.status.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class RegisterVehicleUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun VehicleTypeSelector(
        selectedType: VehicleType,
        onTypeSelected: (VehicleType) -> Unit
    ) {
        Row {
            VehicleType.entries.filter { it != VehicleType.NINGUNO }.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.displayName) }
                )
            }
        }
    }

    @Test
    fun vehicleTypeChips_displayCorrectLabels() {
        setContent {
            VehicleTypeSelector(
                selectedType = VehicleType.AUTOMOVIL,
                onTypeSelected = {}
            )
        }

        composeRule.onNodeWithText("Automóvil").assertIsDisplayed()
        composeRule.onNodeWithText("Motocicleta").assertIsDisplayed()
        composeRule.onNodeWithText("Camioneta").assertIsDisplayed()
    }

    @Test
    fun vehicleTypeChip_triggersCallbackOnSelection() {
        var selected: VehicleType = VehicleType.NINGUNO
        setContent {
            var currentSelected by remember { mutableStateOf(VehicleType.AUTOMOVIL) }
            VehicleTypeSelector(
                selectedType = currentSelected,
                onTypeSelected = {
                    currentSelected = it
                    selected = it
                }
            )
        }

        composeRule.onNodeWithText("Motocicleta").performClick()
        assertEquals(VehicleType.MOTOCICLETA, selected)
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
