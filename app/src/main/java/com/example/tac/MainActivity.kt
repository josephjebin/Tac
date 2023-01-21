package com.example.tac

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tac.data.Constants
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.task.TasksViewModel
import com.example.tac.ui.theme.TacTheme
import com.example.tac.ui.theme.accent_gray
import com.example.tac.ui.theme.primaryGray
import kotlinx.coroutines.*
import net.openid.appauth.*
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import org.json.JSONException

class MainActivity : ComponentActivity() {
    val TAG = "Tac"
    private var authState: AuthState = AuthState()
    private lateinit var authorizationService : AuthorizationService
    lateinit var authServiceConfig : AuthorizationServiceConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAuthServiceConfig()
        initAuthService()
        if(!restoreState()) {
            attemptAuthorization()
        }

        setContent {
            TacTheme {
                Tac()
            }
        }
    }

    fun persistState() {
        application.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.AUTH_STATE, authState.jsonSerializeString())
            .apply()
    }

    fun restoreState(): Boolean {
        val jsonString = application
            .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(Constants.AUTH_STATE, null)

        if( jsonString != null && !TextUtils.isEmpty(jsonString) ) {
            try {
                authState = AuthState.jsonDeserialize(jsonString)
                return true
            } catch(jsonException: JSONException) { }
        }

        return false
    }

    private fun initAuthServiceConfig() {
        authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(Constants.URL_AUTHORIZATION),
            Uri.parse(Constants.URL_TOKEN_EXCHANGE),
            null,
            Uri.parse(Constants.URL_LOGOUT))
    }

    private fun initAuthService() {
        val appAuthConfiguration = AppAuthConfiguration.Builder()
            .setBrowserMatcher(
                BrowserAllowList(
                    VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                    VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
                )
            ).build()

        authorizationService = AuthorizationService(
            application,
            appAuthConfiguration)
    }

    fun attemptAuthorization() {
        val request = AuthorizationRequest.Builder(
            authServiceConfig,
            Constants.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(Constants.URL_AUTH_REDIRECT)
        ).setScopes(Constants.SCOPE_TASKS).build()

        val authIntent = authorizationService.getAuthorizationRequestIntent(request)

        Log.i(TAG, "Trying to get auth code")

        val authorizationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            run {
                Log.i(TAG, "Result code from activity result: ${result.resultCode}")
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Trying to handle auth response: ${result.data}")
                    handleAuthorizationResponse(result.data!!)
                }
            }
        }

        authorizationLauncher.launch(authIntent)
    }

    private fun handleAuthorizationResponse(intent: Intent) {
        val authorizationResponse: AuthorizationResponse? = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        authState = AuthState(authorizationResponse, error)

        val tokenExchangeRequest = authorizationResponse?.createTokenExchangeRequest()
        if (tokenExchangeRequest != null) {
            authorizationService.performTokenRequest(tokenExchangeRequest) { response, exception ->
                if (exception != null) {
                    authState = AuthState()
                } else {
                    if (response != null) {
                        authState.update(response, exception)
                    }
                }
                persistState()
            }
        }
    }
}

@Composable
fun Tac() {
    TasksAndCalendarScreen()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TasksAndCalendarScreen(tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory)) {
    val uiState by tasksViewModel.uiState.collectAsState()

    Box {
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed))
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        var sheetPeekHeight by remember { mutableStateOf(0.dp) }
        var tasksButtonState by remember { mutableStateOf(0) }

        val taskSheetModifier = when(tasksButtonState) {
            0 -> Modifier.height(0.dp)
            else -> Modifier.wrapContentHeight()
        }
        BottomSheetScaffold(
            sheetContent = {
                TaskSheet(
                    taskSheetModifier = taskSheetModifier,
                    uiState.taskLists,
                    uiState.tasks,
                    uiState.currentSelectedTaskList,
                    onTaskListSelected = { taskList: TaskList ->
                        tasksViewModel.updateCurrentSelectedTaskList(taskList)
                    },
                    onTaskSelected = { taskDao: TaskDao ->
                        tasksViewModel.updateCurrentSelectedTask(taskDao)
                    },
                    onTaskCompleted = { taskDao: TaskDao ->
//                        tas
                    }
                )
            },
            scaffoldState = bottomSheetScaffoldState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetPeekHeight = sheetPeekHeight
        ) {
            Box(modifier = Modifier.padding()) {
                Calendar(tasksViewModel)
            }
        }

        BottomAppBar(modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(accent_gray)) {
            //calendar button
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    tasksButtonState = 0
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.round_calendar_today_24), contentDescription = "Refresh")
            }

            //TODO: replace with enum
            //0 -> minimized
            //1 -> slightly expanded
            //2 -> fully expanded
            val tasksButtonModifier: Modifier
            when(tasksButtonState) {
                0 -> {
                    tasksButtonModifier = Modifier.weight(1f).background(primaryGray)
                    LaunchedEffect(tasksButtonState) {
                        sheetPeekHeight = 0.dp
                    }
                }
                1 -> {
                    LaunchedEffect(tasksButtonState) {
                        sheetPeekHeight = 360.dp
                    }
                    tasksButtonModifier = Modifier.weight(1f).background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryGray,
                                accent_gray
                            )
                        )
                    )
                }
                else -> {
                    tasksButtonModifier = Modifier.weight(1f).background(accent_gray)
                    LaunchedEffect(tasksButtonState) {
                        sheetPeekHeight = screenHeight - 40.dp
                    }
                }
            }

            IconButton(modifier = tasksButtonModifier,
                onClick = {
                    if(tasksButtonState < 2) tasksButtonState++
                    else tasksButtonState--
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.round_task_24), contentDescription = "Refresh")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TacTheme() {
    }
}