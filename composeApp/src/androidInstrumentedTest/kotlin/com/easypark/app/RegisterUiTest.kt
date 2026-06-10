package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.easypark.app.core.presentation.composable.ParkButton
import com.easypark.app.core.presentation.composable.ParkTextField
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class RegisterUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun parkTextField_displaysLabelAndPlaceholder() {
        setContent {
            ParkTextField(
                value = "",
                onValueChange = {},
                label = "Nombre Completo",
                placeholder = "Ingresa tu nombre"
            )
        }

        composeRule.onNodeWithText("Nombre Completo").assertIsDisplayed()
        composeRule.onNodeWithText("Ingresa tu nombre").assertIsDisplayed()
    }

    @Test
    fun parkTextField_updatesTextAndTriggersCallback() {
        var enteredText = ""
        setContent {
            var value by remember { mutableStateOf("") }
            ParkTextField(
                value = value,
                onValueChange = {
                    value = it
                    enteredText = it
                },
                label = "Email",
                placeholder = "correo@ejemplo.com"
            )
        }

        composeRule.onNodeWithText("correo@ejemplo.com").performTextInput("test@easypark.com")
        composeRule.waitForIdle()

        assertEquals("test@easypark.com", enteredText)
    }

    @Test
    fun parkButton_rendersTextAndInvokesClick() {
        var clicked = false
        setContent {
            ParkButton(
                text = "Registrarse",
                onClick = { clicked = true }
            )
        }

        composeRule.onNodeWithText("Registrarse").assertIsDisplayed().performClick()
        assertEquals(true, clicked)
    }

    @Test
    fun parkButton_respectsDisabledState() {
        var clicked = false
        setContent {
            ParkButton(
                text = "Registrarse",
                onClick = { clicked = true },
                enabled = false
            )
        }

        composeRule.onNodeWithText("Registrarse").assertIsNotEnabled()
        composeRule.onNodeWithText("Registrarse").performClick()
        assertEquals(false, clicked)
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
