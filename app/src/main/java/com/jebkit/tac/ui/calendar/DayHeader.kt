package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jebkit.tac.R
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayHeader(selectedDate: LocalDate) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.surface_dark_gray))
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
//                    .border(2.dp, Color.Red),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    OutlinedButton(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        border = BorderStroke(3.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = colorResource(id = R.color.akiflow_dark_purple)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.profile_picture_placeholder),
                            tint = colorResource(id = R.color.surface_dark_gray),
                            modifier = Modifier.size(60.dp),
                            contentDescription = "profile"
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "${selectedDate.dayOfWeek}, ${selectedDate.month} ${selectedDate.dayOfMonth}",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    colorResource(id = R.color.akiflow_lavendar),
                                    colorResource(id = R.color.akiflow_app_light_purple)
                                )
                            )
                        ),
                        fontSize = 20.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.search_icon),
                        tint = colorResource(id = R.color.google_text_gray),
                        contentDescription = "search"
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.round_calendar_today_24),
                        tint = colorResource(id = R.color.google_text_gray),
                        contentDescription = ""
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier
                            .width(56.dp)
                            .height(48.dp)
                    ) {
                        TextField(
                            value = "1D",
                            onValueChange = {},
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(textColor = colorResource(id = R.color.google_text_gray)),
                            textStyle = TextStyle(textAlign = TextAlign.Center)
                        )

                        ExposedDropdownMenu(expanded = false, onDismissRequest = { /*TODO*/ }) {
                            DropdownMenuItem(onClick = { /*TODO*/ }) {
                            }
                        }
                    }

                }
            }

            Divider(
                color = Color.Black,
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
            )

        }
    }
}

@Preview
@Composable
fun DayHeaderPreview() {
    DayHeader(selectedDate = LocalDate.now())
}