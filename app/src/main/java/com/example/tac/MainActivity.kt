package com.example.tac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.task.TaskViewModel
import com.example.tac.ui.theme.TacTheme
import com.google.api.services.tasks.model.TaskList



const val TAG = "Tac"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TacTheme() {
                Tac()
            }
        }
    }
}

@Composable
fun Tac(taskViewModel: TaskViewModel = TaskViewModel()) {
    val uiState by taskViewModel.uiState.collectAsState()
    TasksAndCalendarScreen(uiState.taskLists)

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TasksAndCalendarScreen(projects: List<TaskList>) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        sheetContent = {
            TaskSheet(projects)
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) { padding ->  // We need to pass scaffold's inner padding to content. That's why we use Box.
        Box(modifier = Modifier.padding(padding)) {
            Calendar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TacTheme() {
    }
}