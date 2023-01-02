package com.example.tac.data.login

data class GoogleOAuth2ServerResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)
