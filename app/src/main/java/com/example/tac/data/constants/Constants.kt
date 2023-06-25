package com.example.tac.data.constants

class Constants {
    companion object {
        val SHARED_PREFERENCES_NAME = "AUTH_STATE_PREFERENCE"
        val AUTH_STATE = "AUTH_STATE"
        val CLIENT_ID = "518856815930-2n9oqh8e6j7f20pt79ckcsmuasldqbqr.apps.googleusercontent.com"

        val URL_AUTHORIZATION = "https://accounts.google.com/o/oauth2/auth"
        val URL_TOKEN_EXCHANGE = "https://oauth2.googleapis.com/token"
        val URL_AUTH_REDIRECT = "com.example.tac:/oauth2redirect"
        val URL_LOGOUT = "https://accounts.google.com/o/oauth2/revoke?token="

        val SCOPE_TASKS = "https://www.googleapis.com/auth/tasks"
        val URL_TASKS = "https://tasks.googleapis.com/tasks/v1/"

        val SCOPE_CALENDAR = "https://www.googleapis.com/auth/calendar"
        val URL_CALENDAR = "https://www.googleapis.com/calendar/v3/"

        val URL_LOGOUT_REDIRECT = "com.example.tac:/logout"
    }
}