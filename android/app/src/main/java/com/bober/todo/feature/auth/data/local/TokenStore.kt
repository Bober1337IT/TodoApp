package com.bober.todo.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class TokenStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val cachedAccessToken = AtomicReference<String?>(null)

    val accessToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    val email: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_EMAIL]
    }

    fun getAccessToken(): String? = cachedAccessToken.get()

    suspend fun hydrate() {
        cachedAccessToken.set(dataStore.data.first()[KEY_ACCESS_TOKEN])
    }

    suspend fun saveSession(
        accessToken: String,
        tokenType: String,
        email: String,
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_TOKEN_TYPE] = tokenType
            prefs[KEY_EMAIL] = email
        }
        cachedAccessToken.set(accessToken)
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_TOKEN_TYPE)
            prefs.remove(KEY_EMAIL)
        }
        cachedAccessToken.set(null)
    }

    private companion object {
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_TOKEN_TYPE = stringPreferencesKey("token_type")
        val KEY_EMAIL = stringPreferencesKey("email")
    }
}
