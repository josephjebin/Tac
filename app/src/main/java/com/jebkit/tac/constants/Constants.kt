package com.jebkit.tac.constants

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Constants {
    companion object {
        val hourHeight = 64.dp
        val dpPerMinute: Dp = hourHeight / 60
        val JSON_HEADER = "(Tac data:\n"
    }
}