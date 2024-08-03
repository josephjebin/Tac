package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun PlanComposable(
    title: String,
    description: String?,
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
            .testTag("PlanComposable: $title")
    ) {
        Row(
            modifier = Modifier.wrapContentHeight()
                .padding(2.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.caption,
                text = "${start.format(EventTimeFormatter)} - ${end.format(EventTimeFormatter)}:"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier.align(Alignment.Top),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                text = title
            )
        }

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}