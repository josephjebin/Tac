package com.example.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.Plan
import java.time.format.DateTimeFormatter


@Composable
fun PlanComposable(
    plan: Plan,
    modifier: Modifier = Modifier,
) {
    val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 2.dp, bottom = 2.dp)
            .background(plan.color, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Text(
            text = "${plan.start.format(EventTimeFormatter)} - ${plan.end.format(EventTimeFormatter)}",
            style = MaterialTheme.typography.caption,
        )

        Text(
            text = plan.name,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = plan.description,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}