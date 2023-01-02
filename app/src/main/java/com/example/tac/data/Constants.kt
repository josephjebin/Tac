package com.example.tac.data

object Constants {
    const val TAG = "LoginActivity"
    /***********************************************************
     * Attributes
     */
    /**
     * You client id, you have it from the google console when you register your project
     * https://console.developers.google.com/a
     */
    const val CLIENT_ID =
        "518856815930-io2di0gci3l6b4o9ft1fvg67qc47c5q6.apps.googleusercontent.com"

    /**
     * The redirect uri you have define in your google console for your project
     */
    const val REDIRECT_URI =
        "com.example.tac:/oauth2redirect"

    /**
     * The redirect root uri you have define in your google console for your project
     * It is also the scheme your Main Activity will react
     */
    const val REDIRECT_URI_ROOT = "com.example.tac"

    /**
     * You are asking to use a code when authorizing
     */
    const val CODE = "code"

    /**
     * You are receiving an error when authorizing, it's embedded in this field
     */
    const val ERROR_CODE = "error"

    /**
     * GrantType:You are using a code when retrieveing the token
     */
    const val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"

    /**
     * GrantType:You are using a refresh_token when retrieveing the token
     */
    const val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"

    /**
     * The scope: what do we want to use
     * Here we want to be able to do anything on the user's GDrive
     */
    const val API_SCOPE = "https://www.googleapis.com/auth/tasks"
}