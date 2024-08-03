package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.jebkit.tac.R
import java.time.LocalDate

@Composable
fun DayHeader(selectedDate: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.surface_dark_gray))
    ) {
        Text(
            text = "${selectedDate.dayOfWeek}",
            color = colorResource(id = R.color.google_text_white)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = "${selectedDate.month}",
            color = colorResource(id = R.color.google_text_white)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = "${selectedDate.dayOfMonth}",
            color = colorResource(id = R.color.google_text_white)
        )
    }
}