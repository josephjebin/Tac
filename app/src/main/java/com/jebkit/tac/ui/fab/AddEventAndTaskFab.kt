package com.jebkit.tac.ui.fab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jebkit.tac.R

@Composable
fun AddEventAndTaskFab() {

}

@Composable
fun DialogButtonStack(
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { /*TODO*/ }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.round_calendar_today_24),
                    contentDescription = "Add event"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

//            Button(
//                onClick = { /*TODO*/ },
//                shape = RoundedCornerShape(90) ,
//                contentPadding = PaddingValues(0.dp)
//            ) {
            Image(
                painter = painterResource(id = R.drawable.google_task_button),
                contentDescription = "Add task",
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(0.dp)
            )
//            }
        }
    }
}

@Preview
@Composable
fun DialogButtonStackPreview() {
    DialogButtonStack(
        onDismissRequest = {}
    )
}