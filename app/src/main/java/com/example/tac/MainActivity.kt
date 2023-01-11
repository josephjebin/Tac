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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tac.data.Constants
import com.example.tac.data.tasks.TaskList
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.task.TasksViewModel
import com.example.tac.ui.theme.TacTheme
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
//        if(!restoreState()) {
            attemptAuthorization()
//        }

        setContent {
            TacTheme {
                Tac(authState, authorizationService)
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

        Log.e(TAG, "trying to get auth code")

        val authorizationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            run {
                Log.e(TAG, "result code from activity result: ${result.resultCode}")
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.e(TAG, "trying to handle auth response: ${result.data}")
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
        Log.e(TAG, "Auth code: ${authorizationResponse?.accessToken}")

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
fun Tac(authState: AuthState, authorizationService : AuthorizationService, tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory)) {
    val uiState by tasksViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    TasksAndCalendarScreen(tasksViewModel, uiState.taskLists)

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TasksAndCalendarScreen(tasksViewModel: TasksViewModel, projects: List<TaskList?>?) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        sheetContent = {
            TaskSheet(projects)
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) { padding ->  // We need to pass scaffold's inner padding to content. That's why we use Box.
        Box(modifier = Modifier.padding(padding)) {
            Calendar(tasksViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TacTheme() {
    }
}