package com.example.tac

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tac.data.creds.Creds
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.theme.TacTheme
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.TasksScopes
import com.google.api.services.tasks.model.TaskList
import java.io.IOException
import java.io.StringReader

enum class AuthorizationRequestCodes {
    INSERT_AND_READ_DATA,
    UPDATE_AND_READ_DATA,
    DELETE_DATA
}


const val TAG = "Tac"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        signInToGoogle(AuthorizationRequestCodes.INSERT_AND_READ_DATA)
        val projects = everything(GoogleNetHttpTransport.newTrustedTransport())
        setContent {
            TacTheme() {
                TasksAndCalendarScreen(projects)
            }
        }
    }
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val SCOPES = listOf(TasksScopes.TASKS_READONLY)


    @Throws(IOException::class)
    fun everything(HTTP_TRANSPORT: NetHttpTransport): MutableList<TaskList> {
        // Load client secrets.
        val clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, StringReader(Creds().getGoogleCreds()))


        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
//            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
//            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        val ab: AuthorizationCodeInstalledApp =
            object : AuthorizationCodeInstalledApp(flow, receiver) {
                @Throws(IOException::class)
                override fun onAuthorization(authorizationUrl: AuthorizationCodeRequestUrl) {
                    val url = authorizationUrl.build()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(browserIntent)
                }
            }



        val service =
        Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, ab.authorize("user"))
            .setApplicationName("Tac").build()

        return service.tasklists().list().execute().items
    }

//    private fun signInToGoogle(requestCode: AuthorizationRequestCodes) {
//        if (oAuthPermissionsApproved()) {
//            performActionForRequestCode(requestCode)
//        } else {
//            requestCode.let {
//                GoogleSignIn.requestPermissions(
//                    this,
//                    requestCode.ordinal,
//                    getGoogleAccount()
//                )
//            }
//        }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (resultCode) {
//            RESULT_OK -> {
//                val postSignInAction = AuthorizationRequestCodes.values()[requestCode]
//                postSignInAction.let {
//                    performActionForRequestCode(postSignInAction)
//                }
//            }
//            else -> oAuthErrorMsg(requestCode, resultCode)
//        }
//    }
//
//    /**
//     * Runs the desired method, based on the specified request code. The request code is typically
//     * passed to the Fit sign-in flow, and returned with the success callback. This allows the
//     * caller to specify which method, post-sign-in, should be called.
//     *
//     * @param requestCode The code corresponding to the action to perform.
//     */
//    private fun performActionForRequestCode(requestCode: AuthorizationRequestCodes) = when (requestCode) {
//        AuthorizationRequestCodes.INSERT_AND_READ_DATA -> Log.d(TAG, "Signed in!")
//        else -> {Log.d(TAG, "Can't sign in")}
//    }
//
//    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
//        val message = """
//            There was an error signing into Fit. Check the troubleshooting section of the README
//            for potential issues.
//            Request code was: $requestCode
//            Result code was: $resultCode
//        """.trimIndent()
//        Log.e(TAG, message)
//    }
//
//    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount())
//
//    /**
//     * Gets a Google account for use in creating the Fitness client. This is achieved by either
//     * using the last signed-in account, or if necessary, prompting the user to sign in.
//     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
//     * return `null` if there has been no sign in before.
//     */
//    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TasksAndCalendarScreen(projects: MutableList<TaskList>) {
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