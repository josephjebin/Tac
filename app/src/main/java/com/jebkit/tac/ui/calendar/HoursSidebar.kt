package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.jebkit.tac.R

@Composable
fun HoursSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    val HourFormatter = DateTimeFormatter.ofPattern("h a")
    Text(
        text = time.format(HourFormatter),
        color = colorResource(R.color.google_text_gray),
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
    )
}

@Composable
fun HoursSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { HoursSidebarLabel(time = it) },
) {
    Column(modifier = modifier) {
        val startTime = LocalTime.MIN
        repeat(24) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}