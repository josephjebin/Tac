package com.jebkit.tac.ui.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class TacTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sanity() {
        composeTestRule.setContent {
            Tac()
        }

        composeTestRule.onNodeWithText("Work").assertExists()
    }

    @Test
    fun schedule_draggingScheduledTask_updatesScheduledTask() {
        composeTestRule.setContent {
            Tac()
        }

        composeTestRule.onNodeWithTag("PlanComposable: Work").performTouchInput {
            down(center)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            moveBy(Offset(0f, y = 64.dp.toPx()), 0)
            advanceEventTime(1000)
            up()
        }

        composeTestRule.onNodeWithText("3:05 AM - 6:05 AM").assertExists()
    }
}