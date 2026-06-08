package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.easypark.app.bookingconfirmation.presentation.composable.BookingRow
import com.easypark.app.bookingconfirmation.presentation.composable.PaymentOption
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class BookingConfirmationUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun bookingRowDisplaysLabelAndValue() {
        setContent {
            BookingRow(
                label = "Duracion",
                value = "2 Horas"
            )
        }

        composeRule.onNodeWithText("Duracion").assertIsDisplayed()
        composeRule.onNodeWithText("2 Horas").assertIsDisplayed()
    }

    @Test
    fun paymentOptionInvokesClickWhenSelectedByUser() {
        var selectedPayment = "Efectivo"

        setContent {
            Row(modifier = Modifier.height(48.dp)) {
                PaymentOption(
                    text = "Efectivo",
                    isSelected = selectedPayment == "Efectivo",
                    onClick = { selectedPayment = "Efectivo" },
                    modifier = Modifier.weight(1f)
                )
                PaymentOption(
                    text = "QR",
                    isSelected = selectedPayment == "QR",
                    onClick = { selectedPayment = "QR" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        composeRule.onNodeWithText("QR").assertIsDisplayed().performClick()

        assertEquals("QR", selectedPayment)
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
