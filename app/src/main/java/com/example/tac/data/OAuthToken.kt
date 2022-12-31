package com.example.tac.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.api.client.json.Json

/**
 * Created by Mathias Seguy - Android2EE on 06/01/2017.
 */
class OAuthToken {
    /***********************************************************
     * Getters and Setters
     */
    /***********************************************************
     * Attributes
     */
    @Json(name = "access_token")
    var accessToken: String? = null

    @Json(name = "token_type")
    var tokenType: String? = null

    @Json(name = "expires_in")
    val expiresIn: Long = 0
    private var expiredAfterMilli: Long = 0

    @Json(name = "refresh_token")
    var refreshToken: String? = null

    /***********************************************************
     * Managing Persistence
     */
    fun save() {
        Log.e(TAG, "Savng the following element $this")
        //update expired_after
        expiredAfterMilli = System.currentTimeMillis() + expiresIn * 1000
        Log.e(
            TAG,
            "Savng the following element and expiredAfterMilli =" + expiredAfterMilli + " where now=" + System.currentTimeMillis() + " and expired in =" + expiresIn
        )
        val sp: SharedPreferences = MyApplication.instance.getSharedPreferences(
            OAUTH_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE
        )
        val ed = sp.edit()
        ed.putString(SP_TOKEN_KEY, accessToken)
        ed.putString(SP_TOKEN_TYPE_KEY, tokenType)
        ed.putLong(SP_TOKEN_EXPIRED_AFTER_KEY, expiredAfterMilli)
        ed.putString(SP_REFRESH_TOKEN_KEY, refreshToken)
        ed.commit()
    }

    fun setExpiredAfterMilli(expiredAfterMilli: Long) {
        this.expiredAfterMilli = expiredAfterMilli
    }

    override fun toString(): String {
        val sb = StringBuffer("OAuthToken{")
        sb.append("accessToken='").append(accessToken).append('\'')
        sb.append(", tokenType='").append(tokenType).append('\'')
        sb.append(", expires_in=").append(expiresIn)
        sb.append(", expiredAfterMilli=").append(expiredAfterMilli)
        sb.append(", refreshToken='").append(refreshToken).append('\'')
        sb.append('}')
        return sb.toString()
    }

    /***********************************************************
     * Factory Pattern
     */
    object Factory {
        private const val TAG = "OAuthToken.Factory"
        fun create(): OAuthToken? {
            var expiredAfter: Long = 0
            val sp: SharedPreferences = MyApplication.instance.getSharedPreferences(
                OAUTH_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE
            )
            return if (sp.contains(SP_TOKEN_EXPIRED_AFTER_KEY)) {
                Log.e(TAG, "sp.contains(SP_TOKEN_EXPIRED_AFTER)")
                expiredAfter = sp.getLong(SP_TOKEN_EXPIRED_AFTER_KEY, 0)
                val now = System.currentTimeMillis()
                Log.e(TAG, "Delta : " + (now - expiredAfter))
                if (expiredAfter == 0L || now > expiredAfter) {
                    Log.e(TAG, "expiredAfter==0||now>expiredAfter, token has expired")
                    //flush token in the SP
                    val ed = sp.edit()
                    ed.putString(SP_TOKEN_KEY, null)
                    ed.commit()
                    //rebuild the object according to the SP
                    val oauthToken = OAuthToken()
                    oauthToken.accessToken = null
                    oauthToken.tokenType =
                        sp.getString(SP_TOKEN_TYPE_KEY, null)
                    oauthToken.refreshToken =
                        sp.getString(SP_REFRESH_TOKEN_KEY, null)
                    oauthToken.setExpiredAfterMilli(sp.getLong(SP_TOKEN_EXPIRED_AFTER_KEY, 0))
                    oauthToken
                } else {
                    Log.e(
                        TAG,
                        "NOT (expiredAfter==0||now<expiredAfter) current case, token is valid"
                    )
                    //rebuild the object according to the SP
                    val oauthToken = OAuthToken()
                    oauthToken.accessToken = sp.getString(SP_TOKEN_KEY, null)
                    oauthToken.tokenType =
                        sp.getString(SP_TOKEN_TYPE_KEY, null)
                    oauthToken.refreshToken =
                        sp.getString(SP_REFRESH_TOKEN_KEY, null)
                    oauthToken.setExpiredAfterMilli(sp.getLong(SP_TOKEN_EXPIRED_AFTER_KEY, 0))
                    oauthToken
                }
            } else {
                null
            }
        }
    }

    companion object {
        private const val TAG = "OAuthToken"

        /***********************************************************
         * Constants
         */
        private const val OAUTH_SHARED_PREFERENCE_NAME = "OAuthPrefs"
        private const val SP_TOKEN_KEY = "token"
        private const val SP_TOKEN_TYPE_KEY = "token_type"
        private const val SP_TOKEN_EXPIRED_AFTER_KEY = "expired_after"
        private const val SP_REFRESH_TOKEN_KEY = "refresh_token"
    }
}