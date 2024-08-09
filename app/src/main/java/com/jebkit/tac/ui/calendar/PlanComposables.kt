package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jebkit.tac.ui.theme.akiflow_lavender
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun EventComposable(
    title: String,
    description: String?,
    color: Color,
    start: LocalTime,
    end: LocalTime,
    modifier: Modifier = Modifier
) {
    val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    Column(
        modifier = modifier
            .background(color, shape = RoundedCornerShape(8.dp))
            .testTag("PlanComposable: $title")
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
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

@Composable
fun ScheduledTaskComposable(
    modifier: Modifier = Modifier,
    title: String,
    isCompleted: Boolean,
    //toggleScheduledTaskCompletion has a default value to let draggable spawn this composable
    //it's fine cuz a user can't click the checkbox while dragging
    toggleScheduledTaskCompletion: () -> Unit = {},
    description: String?,
    color: Color,
    start: LocalTime,
    end: LocalTime
) {
    val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    Row(
        modifier = modifier
            .background(color, shape = RoundedCornerShape(8.dp))
            .testTag("PlanComposable: $title")
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
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

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.CenterVertically),
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { toggleScheduledTaskCompletion() },
            )
        }

    }
}

@Preview
@Composable
fun PlanComposablePreview() {
    EventComposable(
        title = "Testing",
        description = "test",
        color = akiflow_lavender,
        start = LocalTime.now(),
        end = LocalTime.now().plusMinutes(30),
        modifier = Modifier.height(120.dp)
    )
}