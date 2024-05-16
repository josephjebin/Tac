package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import com.example.tac.ui.theme.onSurfaceGray
import java.time.ZonedDateTime

open class Plan(
    var name: String = "",
    val color: Color = onSurfaceGray,
    var start: ZonedDateTime = ZonedDateTime.now(),
    var end: ZonedDateTime = ZonedDateTime.now(),
    var description: String = ""
) {

}
