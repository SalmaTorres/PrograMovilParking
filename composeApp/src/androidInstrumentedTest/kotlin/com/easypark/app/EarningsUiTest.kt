package com.easypark.app

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.easypark.app.earnings.domain.model.EarningTransactionModel
import com.easypark.app.earnings.presentation.composable.EarningItemRow
import com.easypark.app.earnings.presentation.composable.EarningsStatCard
import com.easypark.app.earnings.presentation.composable.EarningsSummaryCard
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_calendar
import kotlinproject.composeapp.generated.resources.ic_garage
import org.junit.Rule
import org.junit.Test

class EarningsUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun earningsSummaryCardShowsTotalAndPercentage() {
        setContent {
            EarningsSummaryCard(
                total = 1250.0,
                percentage = 12.5
            )
        }

        composeRule.onNodeWithText("Ingresos").assertIsDisplayed()
        composeRule.onNodeWithText("$1250.00").assertIsDisplayed()
        composeRule.onNodeWithText("+12.5%").assertIsDisplayed()
    }

    @Test
    fun earningsStatsAndTransactionShowMainInformation() {
        setContent {
            Column {
                EarningsStatCard(
                    title = "Reservas activas",
                    value = "4",
                    subValue = "+2",
                    icon = Res.drawable.ic_calendar
                )
                EarningsStatCard(
                    title = "Espacios ocupados",
                    value = "9",
                    subValue = "/10",
                    icon = Res.drawable.ic_garage,
                    isAlert = true
                )
                EarningItemRow(
                    transaction = EarningTransactionModel(
                        id = 1,
                        date = "lunes",
                        label = "Espacio A1",
                        amount = 35.0,
                        currency = "BS"
                    )
                )
            }
        }

        composeRule.onNodeWithText("Reservas activas").assertIsDisplayed()
        composeRule.onNodeWithText("4").assertIsDisplayed()
        composeRule.onNodeWithText("Espacios ocupados").assertIsDisplayed()
        composeRule.onNodeWithText("Capacidad limitada").assertIsDisplayed()
        composeRule.onNodeWithText("LUNES").assertIsDisplayed()
        composeRule.onNodeWithText("Espacio A1").assertIsDisplayed()
        composeRule.onNodeWithText("35.0 BS").assertIsDisplayed()
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}

