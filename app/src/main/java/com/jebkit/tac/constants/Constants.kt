package com.jebkit.tac.constants

class Constants {
    companion object {
//        val SHARED_PREFERENCES_NAME = "AUTH_STATE_PREFERENCE"
//        val AUTH_STATE = "AUTH_STATE"
//
//        val URL_AUTHORIZATION = "https://accounts.google.com/o/oauth2/auth"
//        val URL_TOKEN_EXCHANGE = "https://oauth2.googleapis.com/token"
////        val URL_AUTH_REDIRECT = "com.example.tac:/oauth2redirect"
//        val URL_LOGOUT = "https://accounts.google.com/o/oauth2/revoke?token="
//
//        val SCOPE_TASKS = "https://www.googleapis.com/auth/tasks"
//        val URL_TASKS = "https://tasks.googleapis.com/tasks/v1/"
//
//        val SCOPE_CALENDAR = "https://www.googleapis.com/auth/calendar"
//        val URL_CALENDAR = "https://www.googleapis.com/calendar/v3/"
//        val URL_CALENDAR_WITHOUT_HOST = "www.googleapis.com"

//        val URL_LOGOUT_REDIRECT = "com.example.tac:/logout"

        val TASK_JSON_HEADER = """
            Tac Data
            1. Please don’t modify this section. Modifying this section can orphan associated scheduled tasks.
            2. Please put all of your notes above this section. Tac will delete any content after this section when repairing malformed data.""".trim()
        val SCHEDULEDTASK_JSON_HEADER = """
            Tac Data
            1. Please don’t modify this section. Modifying this section can orphan this scheduled task from its parent task.
            2. Please put all of your notes above this section. Tac will delete any content after this section when repairing malformed data.""".trim()
    }
}