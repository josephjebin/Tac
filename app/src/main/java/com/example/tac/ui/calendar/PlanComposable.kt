package com.example.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.Plan
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun PlanComposable(
    name: String,
    description: String,
    color: Color,
    start: LocalTime,
    end: LocalTime,
    modifier: Modifier = Modifier,
) {
    val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color, shape = RoundedCornerShape(4.dp))
            .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
            .testTag("PlanComposable: $name")
    ) {
        Text(
            text = "${start.format(EventTimeFormatter)} - ${end.format(EventTimeFormatter)}",
            style = MaterialTheme.typography.caption,
        )

        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = description,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}