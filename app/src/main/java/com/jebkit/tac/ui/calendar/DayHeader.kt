package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jebkit.tac.R
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayHeader(selectedDate: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.surface_dark_gray))
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            shape = CircleShape,
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.blank_profile_picture),
                tint = colorResource(id = R.color.akiflow_app_light_purple),
                modifier = Modifier.size(44.dp),
                contentDescription = "profile"
            )
        }

        Text(
            text = "${selectedDate.dayOfWeek}, ${selectedDate.month} ${selectedDate.dayOfMonth}",
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(colorResource(id = R.color.akiflow_lavendar), colorResource(id = R.color.akiflow_dark_purple))
                )
            )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.round_calendar_today_24),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(8.dp))

            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.width(40.dp)
            ) {
                TextField(
                    value = "1",
                    onValueChange = {},
                    singleLine = true
                )

                ExposedDropdownMenu(expanded = false, onDismissRequest = { /*TODO*/ }) {
                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DayHeaderPreview() {
    DayHeader(selectedDate = LocalDate.now())
}