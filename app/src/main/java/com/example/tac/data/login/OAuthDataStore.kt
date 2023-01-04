package com.example.tac.data.login

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.IOException

private const val TOKEN_DATASTORE = "layout_preferences "
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN_DATASTORE)

class OAuthDataStore(context: Context) {
    private val access_token = stringPreferencesKey("access_token")
    private val expires_in = intPreferencesKey("expires_in")
    private val refresh_token = stringPreferencesKey("refresh_token")
    private val scope = stringPreferencesKey("scope")
    private val token_type = stringPreferencesKey("token_type")

    val accessTokenFlow: Flow<String> = context.dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[access_token] ?: ""
    }


    suspend fun saveTokenToTokenDataStore(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[access_token] = token
        }
    }
}