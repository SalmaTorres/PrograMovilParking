package com.easypark.app

import ReservationModel
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easypark.app.core.domain.model.PriceModel
import com.easypark.app.reservationsummary.presentation.composable.DetailRow
import com.easypark.app.reservationsummary.presentation.screen.ReservationItemCard
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule

class ReservationSummaryUiTest {

    @get:Rule(order = 0)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(ComposeTestActivity::class.java)

    @Test
    fun detailRow_displaysLabelAndValue() {
        setContent {
            DetailRow(
                icon = Icons.Default.Info,
                label = "Espacio Reservado",
                value = "Nro 12"
            )
        }

        composeRule.onNodeWithText("Espacio Reservado").assertIsDisplayed()
        composeRule.onNodeWithText("Nro 12").assertIsDisplayed()
    }

    @Test
    fun reservationItemCard_displaysGeneralInformation() {
        setContent {
            ReservationItemCard(
                reservation = reservationModel("ACTIVE"),
                onCheckOutClick = {},
                onCancelReservation = {}
            )
        }

        composeRule.onNodeWithText("Parqueo Central").assertIsDisplayed()
        composeRule.onNodeWithText("Av. America 123").assertIsDisplayed()
        composeRule.onNodeWithText("Nro 15").assertIsDisplayed()
        composeRule.onNodeWithText("ABC-123").assertIsDisplayed()
        composeRule.onNodeWithText("Automovil").assertIsDisplayed()
        composeRule.onNodeWithText("Bs 12.0").assertIsDisplayed()
        composeRule.onNodeWithText("QR").assertIsDisplayed()
    }

    @Test
    fun reservationItemCard_showsPendingBannerWhenStatusIsPending() {
        setContent {
            ReservationItemCard(
                reservation = reservationModel("PENDIENTE"),
                onCheckOutClick = {},
                onCancelReservation = {}
            )
        }

        composeRule.onNodeWithText("Reserva PENDIENTE").assertIsDisplayed()
    }

    @Test
    fun reservationItemCard_showsCheckOutButtonOnlyWhenOccupied() {
        var checkOutClicked = false
        setContent {
            ReservationItemCard(
                reservation = reservationModel("OCUPADO"),
                onCheckOutClick = { checkOutClicked = true },
                onCancelReservation = {}
            )
        }

        composeRule.onNodeWithText("Finalizar Estancia (Check-out)").assertIsDisplayed().performClick()
        assertEquals(true, checkOutClicked)
    }

    private fun reservationModel(status: String): ReservationModel {
        return ReservationModel(
            id = 1,
            parkingName = "Parqueo Central",
            address = "Av. America 123",
            spaceNumber = 15,
            startTime = 1718000000000L,
            endTime = 1718007200000L,
            totalPrice = PriceModel(amount = 12.0),
            paymentMethod = "QR",
            status = status,
            vehiclePlate = "ABC-123",
            vehicleType = "Automovil"
        )
    }

    private fun setContent(content: @Composable () -> Unit) {
        activityRule.scenario.onActivity { activity ->
            activity.setContent(content = content)
        }
        composeRule.waitForIdle()
    }
}
